import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(private router: Router) {}

    canActivate(): boolean | UrlTree {
        const token = localStorage.getItem('token');
        if (token) {
            // Basic token validation: check if token is a valid JWT and not expired
            try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const exp = payload.exp;
            if (exp && Date.now() < exp * 1000) {
                return true;
            }
            } catch (e) {
            // Invalid token format
            }
        }
        // Redirect to login if not authenticated
        return this.router.createUrlTree(['/login']);
    }
}