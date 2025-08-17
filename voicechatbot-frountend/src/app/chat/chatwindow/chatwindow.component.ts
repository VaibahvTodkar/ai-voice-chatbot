import { Component, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotService } from '../../_shared/chatbot.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-chatwindow',
  imports: [CommonModule, FormsModule],
  templateUrl: './chatwindow.component.html',
  styleUrls: ['./chatwindow.component.css']
})
export class ChatwindowComponent implements AfterViewChecked {
  @ViewChild('chatWindow') chatWindow!: ElementRef;

  userInput: string = '';
  isLoading = false;
  isRecording = false;

  private recorder: {
    stream: MediaStream,
    audioContext: AudioContext,
    processor: ScriptProcessorNode,
    source: MediaStreamAudioSourceNode,
    chunks: Float32Array[]
  } | null = null;

  private readonly CHAT_API = "http://localhost:8080/api/v1/chat/chat";

  constructor(
    public chatbotService: ChatbotService,
    private http: HttpClient
  ) { }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    if (this.chatWindow) {
      this.chatWindow.nativeElement.scrollTop = this.chatWindow.nativeElement.scrollHeight;
    }
  }

  // ================== CHAT SEND ==================
  private sendToChatApi(body: any, userMessageText: string, onComplete?: (botMessage: any) => void): void {
    let userMessage = null;

    if (userMessageText) {
      userMessage = {
        role: 'USER' as 'USER',
        text: userMessageText,
        timestamp: new Date()
      };
      this.chatbotService.messages.push(userMessage);
    }

    this.isLoading = true;

    this.http.post(this.CHAT_API, body).subscribe({
      next: (botMessage: any) => {
        // Push assistant response
        this.chatbotService.messages.push({
          role: 'ASSISTANT',
          text: botMessage.textResponse || '🤖 No response text',
          timestamp: new Date(botMessage.timestamp || new Date())
        });

        // Play audio if exists
        if (botMessage.audioBase64) {
          this.playBase64Audio(botMessage.audioBase64);
        }

        this.isLoading = false;

        if (onComplete) onComplete(botMessage);
      },
      error: (err: any) => {
        console.error('Error fetching chatbot reply:', err);
        this.chatbotService.messages.push({
          role: 'ASSISTANT',
          text: '⚠️ Unable to fetch chatbot response. Please try again later.',
          timestamp: new Date()
        });
        this.isLoading = false;
      }
    });
  }

  sendMessage(): void {
    const input = this.userInput.trim();
    if (!input) return;

    this.userInput = '';
    this.sendToChatApi({ text: input }, input);
  }

  // ================== AUDIO RECORDING (PCM WAV) ==================
  startRecording() {
    console.log("🎤 startRecording() called");
    navigator.mediaDevices.getUserMedia({ audio: true }).then(stream => {
      const audioContext = new AudioContext();
      const source = audioContext.createMediaStreamSource(stream);
      const processor = audioContext.createScriptProcessor(4096, 1, 1);

      const chunks: Float32Array[] = [];
      processor.onaudioprocess = e => {
        chunks.push(new Float32Array(e.inputBuffer.getChannelData(0)));
      };

      source.connect(processor);
      processor.connect(audioContext.destination);

      this.recorder = { stream, audioContext, processor, source, chunks };
      this.isRecording = true;
    });
  }

  stopRecording() {
    console.log("⏹ stopRecording() called");
    if (!this.recorder) {
      console.warn("No active recording to stop.");
      return;
    }

    const { stream, processor, source, audioContext, chunks } = this.recorder;

    try {
      processor.disconnect();
      source.disconnect();
      stream.getTracks().forEach(track => track.stop());
    } catch (e) {
      console.error("Error cleaning up audio nodes", e);
    }

    if (chunks.length > 0) {
      const flat = this.flattenArray(chunks);
      const wavBuffer = this.encodeWAV(flat, audioContext.sampleRate);
      const base64Audio = this.arrayBufferToBase64(wavBuffer);

      // Push a temporary message for voice
      const tempMessage = { role: 'USER' as 'USER', text: '🎤 Recording...', timestamp: new Date() };
      this.chatbotService.messages.push(tempMessage);

      // Send to backend and update message when response arrives
      this.sendToChatApi({ base64Audio }, '', (botMessage) => {
        tempMessage.text = botMessage.textReqest || '🤖 No response text';
      });
    } else {
      console.warn("No audio chunks recorded.");
    }

    this.recorder = null;
    this.isRecording = false;
  }

  arrayBufferToBase64(buffer: ArrayBuffer): string {
    let binary = "";
    const bytes = new Uint8Array(buffer);
    const chunkSize = 0x8000; // 32k chunks

    for (let i = 0; i < bytes.length; i += chunkSize) {
      const chunk = bytes.subarray(i, i + chunkSize);
      binary += String.fromCharCode.apply(null, Array.from(chunk));
    }

    return btoa(binary);
  }

  private flattenArray(chunks: Float32Array[]): Float32Array {
    let length = 0;
    chunks.forEach(c => length += c.length);
    const result = new Float32Array(length);
    let offset = 0;
    chunks.forEach(c => {
      result.set(c, offset);
      offset += c.length;
    });
    return result;
  }

  private encodeWAV(samples: Float32Array, sampleRate: number): ArrayBuffer {
    const buffer = new ArrayBuffer(44 + samples.length * 2);
    const view = new DataView(buffer);

    const writeString = (offset: number, str: string) => {
      for (let i = 0; i < str.length; i++) {
        view.setUint8(offset + i, str.charCodeAt(i));
      }
    };

    let offset = 0;
    writeString(offset, 'RIFF'); offset += 4;
    view.setUint32(offset, 36 + samples.length * 2, true); offset += 4;
    writeString(offset, 'WAVE'); offset += 4;
    writeString(offset, 'fmt '); offset += 4;
    view.setUint32(offset, 16, true); offset += 4;
    view.setUint16(offset, 1, true); offset += 2; // PCM
    view.setUint16(offset, 1, true); offset += 2; // mono
    view.setUint32(offset, sampleRate, true); offset += 4;
    view.setUint32(offset, sampleRate * 2, true); offset += 4;
    view.setUint16(offset, 2, true); offset += 2; // block align
    view.setUint16(offset, 16, true); offset += 2; // bits per sample
    writeString(offset, 'data'); offset += 4;
    view.setUint32(offset, samples.length * 2, true); offset += 4;

    let index = 44;
    for (let i = 0; i < samples.length; i++, index += 2) {
      const s = Math.max(-1, Math.min(1, samples[i]));
      view.setInt16(index, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
    }

    return buffer;
  }

  private playBase64Audio(base64Audio: string) {
    const audio = new Audio("data:audio/wav;base64," + base64Audio);
    audio.play();
  }
}




// import { Component, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ChatbotService } from '../../_shared/chatbot.service';
// import { HttpClient } from '@angular/common/http';

// @Component({
//   selector: 'app-chatwindow',
//   imports: [CommonModule, FormsModule],
//   templateUrl: './chatwindow.component.html',
//   styleUrls: ['./chatwindow.component.css']
// })
// export class ChatwindowComponent implements AfterViewChecked {
//   @ViewChild('chatWindow') chatWindow!: ElementRef;

//   userInput: string = '';
//   isLoading = false;
//   isRecording = false;

//   private recorder: {
//     stream: MediaStream,
//     audioContext: AudioContext,
//     processor: ScriptProcessorNode,
//     source: MediaStreamAudioSourceNode,
//     chunks: Float32Array[]
//   } | null = null;

//   private readonly CHAT_API = "http://localhost:8080/api/v1/chat/chat";

//   constructor(
//     public chatbotService: ChatbotService,
//     private http: HttpClient
//   ) { }

//   ngAfterViewChecked(): void {
//     this.scrollToBottom();
//   }

//   private scrollToBottom(): void {
//     if (this.chatWindow) {
//       this.chatWindow.nativeElement.scrollTop = this.chatWindow.nativeElement.scrollHeight;
//     }
//   }

//   // ================== CHAT SEND ==================
//   private sendToChatApi(body: any, userMessageText: string): void {
//     this.chatbotService.messages.push({
//       role: 'USER',
//       text: userMessageText,
//       timestamp: new Date()
//     });

//     this.isLoading = true;

//     this.http.post(this.CHAT_API, body).subscribe({
//       next: (botMessage: any) => {
//         this.chatbotService.messages.push({
//           role: 'ASSISTANT',
//           text: botMessage.textResponse || '🤖 No response text',
//           timestamp: new Date(botMessage.timestamp || new Date())
//         });

//         if (botMessage.audioBase64) {
//           this.playBase64Audio(botMessage.audioBase64);
//         }

//         this.isLoading = false;
//       },
//       error: (err: any) => {
//         console.error('Error fetching chatbot reply:', err);
//         this.chatbotService.messages.push({
//           role: 'ASSISTANT',
//           text: '⚠️ Unable to fetch chatbot response. Please try again later.',
//           timestamp: new Date()
//         });
//         this.isLoading = false;
//       }
//     });
//   }

//   sendMessage(): void {
//     const input = this.userInput.trim();
//     if (!input) return;

//     this.userInput = '';
//     this.sendToChatApi({ text: input }, input);
//   }

//   // ================== AUDIO RECORDING (PCM WAV) ==================
//   startRecording() {
//     console.log("🎤 startRecording() called");
//     navigator.mediaDevices.getUserMedia({ audio: true }).then(stream => {
//       const audioContext = new AudioContext();
//       const source = audioContext.createMediaStreamSource(stream);
//       const processor = audioContext.createScriptProcessor(4096, 1, 1);

//       const chunks: Float32Array[] = [];
//       processor.onaudioprocess = e => {
//         chunks.push(new Float32Array(e.inputBuffer.getChannelData(0)));
//       };

//       source.connect(processor);
//       processor.connect(audioContext.destination);

//       this.recorder = { stream, audioContext, processor, source, chunks };
//       this.isRecording = true;
//     });
//   }

//   stopRecording() {
//     console.log("⏹ stopRecording() called");
//     // 🚫 If no active recorder, just exit (prevents recursive loop)
//     if (!this.recorder) {
//       console.warn("No active recording to stop.");
//       return;
//     }

//     const { stream, processor, source, audioContext, chunks } = this.recorder;

//     // ✅ Stop audio graph
//     try {
//       processor.disconnect();
//       source.disconnect();
//       stream.getTracks().forEach(track => track.stop());
//     } catch (e) {
//       console.error("Error cleaning up audio nodes", e);
//     }

//     // ✅ Encode WAV only if we actually have chunks
//     if (chunks.length > 0) {
//       const flat = this.flattenArray(chunks);
//       const wavBuffer = this.encodeWAV(flat, audioContext.sampleRate);

//       try {
//         // const base64Audio = btoa(
//         //   String.fromCharCode(...new Uint8Array(wavBuffer))
//         // );
//         const base64Audio = this.arrayBufferToBase64(wavBuffer);
//         console.log("Sending base64 audio to backend:", base64Audio);
//         this.sendToChatApi({ base64Audio }, '🎤 Voice message sent');
//       } catch (error) {
//         console.error("Error encoding audio to base64", error);
//       }
//     } else {
//       console.warn("No audio chunks recorded.");
//     }

//     // ✅ Ensure cleanup
//     this.recorder = null;
//     this.isRecording = false;
//   }


//   arrayBufferToBase64(buffer: ArrayBuffer): string {
//     let binary = "";
//     const bytes = new Uint8Array(buffer);
//     const chunkSize = 0x8000; // 32k chunks

//     for (let i = 0; i < bytes.length; i += chunkSize) {
//       const chunk = bytes.subarray(i, i + chunkSize);
//       binary += String.fromCharCode.apply(null, Array.from(chunk));
//     }

//     return btoa(binary);
//   }

//   // Helper: flatten Float32Array[]
//   private flattenArray(chunks: Float32Array[]): Float32Array {
//     let length = 0;
//     chunks.forEach(c => length += c.length);
//     const result = new Float32Array(length);
//     let offset = 0;
//     chunks.forEach(c => {
//       result.set(c, offset);
//       offset += c.length;
//     });
//     return result;
//   }

//   // Helper: PCM → WAV
//   private encodeWAV(samples: Float32Array, sampleRate: number): ArrayBuffer {
//     const buffer = new ArrayBuffer(44 + samples.length * 2);
//     const view = new DataView(buffer);

//     const writeString = (offset: number, str: string) => {
//       for (let i = 0; i < str.length; i++) {
//         view.setUint8(offset + i, str.charCodeAt(i));
//       }
//     };

//     let offset = 0;
//     writeString(offset, 'RIFF'); offset += 4;
//     view.setUint32(offset, 36 + samples.length * 2, true); offset += 4;
//     writeString(offset, 'WAVE'); offset += 4;
//     writeString(offset, 'fmt '); offset += 4;
//     view.setUint32(offset, 16, true); offset += 4;
//     view.setUint16(offset, 1, true); offset += 2; // PCM
//     view.setUint16(offset, 1, true); offset += 2; // mono
//     view.setUint32(offset, sampleRate, true); offset += 4;
//     view.setUint32(offset, sampleRate * 2, true); offset += 4;
//     view.setUint16(offset, 2, true); offset += 2; // block align
//     view.setUint16(offset, 16, true); offset += 2; // bits per sample
//     writeString(offset, 'data'); offset += 4;
//     view.setUint32(offset, samples.length * 2, true); offset += 4;

//     let index = 44;
//     for (let i = 0; i < samples.length; i++, index += 2) {
//       const s = Math.max(-1, Math.min(1, samples[i]));
//       view.setInt16(index, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
//     }

//     return buffer;
//   }

//   private playBase64Audio(base64Audio: string) {
//     const audio = new Audio("data:audio/wav;base64," + base64Audio);
//     audio.play();
//   }
// }
