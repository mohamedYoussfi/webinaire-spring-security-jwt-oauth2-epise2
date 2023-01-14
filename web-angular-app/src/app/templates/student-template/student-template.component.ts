import { Component } from '@angular/core';
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-student-template',
  templateUrl: './student-template.component.html',
  styleUrls: ['./student-template.component.css']
})
export class StudentTemplateComponent {
  constructor(public authService : AuthService) {
  }

  onLogout() {
    this.authService.logout();
  }
}
