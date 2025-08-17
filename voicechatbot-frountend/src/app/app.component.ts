import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { NavComponent } from './_common-components/nav/nav.component';
import { FooterComponent } from "./_common-components/footer/footer.component";

@Component({
  selector: 'app-root',
  imports: [RouterModule, NavComponent, FooterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'voicechatbot-frountend';
}
