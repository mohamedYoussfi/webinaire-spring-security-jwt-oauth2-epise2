import { Component } from '@angular/core';
import {AuthService} from "./services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'students-angular-app';
  constructor(private authService : AuthService, private router : Router) {
    this.authService.loadProfile();
    this.router.navigateByUrl("/admin")
  }
}
