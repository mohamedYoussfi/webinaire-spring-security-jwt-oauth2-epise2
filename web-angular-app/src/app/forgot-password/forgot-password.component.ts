import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../services/auth.service";
import {Route, Router} from "@angular/router";

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit{
  formGroup! : FormGroup;
  state : number =1;
  secretCode : number=0;
  email : string ="";
  errorMessage : any;

  constructor(private fb : FormBuilder, private authService : AuthService, private router : Router) {
  }

  ngOnInit() {
    this.formGroup=this.fb.group({
      email : ['', [Validators.required, Validators.email]],
      secretCode : ['', [Validators.required]],
      password : ['', [Validators.required]],
      confirmPassword : ['', [Validators.required]],
    });
  }

  forgotPassword() {
    this.errorMessage=undefined;
    if(this.state==1){
      this.authService.forgotPassword(this.formGroup.value, this.state).subscribe({
        next : value => {
          this.email=this.formGroup.value.email;
          this.state=2;
        },
        error : err => {
          this.errorMessage=err.error.errorMessage;
        }
      });
    } else if (this.state == 2) {
      this.authService.forgotPassword(this.formGroup.value, this.state).subscribe({
        next : value => {
          this.secretCode=this.formGroup.value.secretCode;
          this.state=3;
        },
        error : err => {
          alert(err.error.errorMessage);
        }
      });

    }
    else if (this.state == 3) {
      this.authService.forgotPassword(this.formGroup.value, this.state).subscribe({
        next : value => {
          this.secretCode=value;
          this.state=3;
          this.router.navigateByUrl("/login");
        },
        error : err => {
          alert(err.error.errorMessage);
        }
      });

    }

  }
}
