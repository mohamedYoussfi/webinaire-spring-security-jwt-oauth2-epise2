import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {AuthService} from "../services/auth.service";
import {Route, Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{
   loginFormGroup! : FormGroup;
   idToken : any;
   errorMessage :any;
  constructor(private fb : FormBuilder, private authservice : AuthService, private router : Router) {
  }
  ngOnInit() {
    this.loginFormGroup=this.fb.group({
      username : this.fb.control(''),
      password : this.fb.control('')
    });
  }

  handleLogin() {
    this.errorMessage=undefined;
    let username=this.loginFormGroup.value.username;
    let password=this.loginFormGroup.value.password;
    this.authservice.login(username,password).subscribe({
      next: response => {
        this.idToken = response;
        this.authservice.authenticateUser(this.idToken, undefined);
        this.authservice.loadUserProfile().subscribe({
          next: value => {
            this.authservice.savePhotoURL(value.photoURL);
            this.router.navigateByUrl("/admin/my-courses");
          },
          error: err => {
            if (err.status == 0) {
              this.errorMessage = "Connection Problem";
            } else if (err.status == 401) {
              this.errorMessage = err.error.errorMessage;
            }
          }
        })
      },
      error :err => {
        this.errorMessage = err.error.errorMessage;
      }
    })
  }
}
