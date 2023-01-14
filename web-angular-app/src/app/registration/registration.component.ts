import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {AuthService} from "../services/auth.service";
import {UniqueUserValidator} from "../validators/unique-user.validator";
import {valuesMatchValidator} from "../validators/passwords.match.validator";
import {UniqueEmailValidator} from "../validators/unique-email.validator";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
  providers : [UniqueUserValidator, UniqueEmailValidator]
})
export class RegistrationComponent implements OnInit{
  registrationFormGroup! : FormGroup;
  errorMessage : any;
  formStatus : number=0;
 constructor(private fb : FormBuilder, private authService : AuthService,
             private uniqueUserValidator : UniqueUserValidator,
             private uniqueEmailValidator : UniqueEmailValidator) {
 }
 ngOnInit() {
   this.registrationFormGroup=this.fb.group({
     username : ['', {asyncValidators :[this.uniqueUserValidator], validators : [Validators.required]}],
     firstName : this.fb.control('', Validators.required),
     lastName : this.fb.control('', Validators.required),
     email : ['',{validators:[Validators.required, Validators.email], asyncValidators : [this.uniqueEmailValidator]}],
     password : this.fb.control('', Validators.required),
     confirmPassword : this.fb.control('', Validators.required),
     gender : this.fb.control('MALE')
   }, {
     validators : valuesMatchValidator('password','confirmPassword')
   });
 }

 get username() {
   return this.registrationFormGroup.controls['username'];
 }
  get firstName() {
    return this.registrationFormGroup.controls['firstName'];
  }
  get lastName() {
    return this.registrationFormGroup.controls['lastName'];
  }
  get email() {
    return this.registrationFormGroup.controls['email'];
  }
  get password() {
    return this.registrationFormGroup.controls['password'];
  }
  get confirmPassword() {
    return this.registrationFormGroup.controls['confirmPassword'];
  }
  get userType() {
    return this.registrationFormGroup.controls['userType'];
  }

  registerUser() {
   this.authService.registerUser(this.registrationFormGroup.value).subscribe({
     next : value => {
       this.formStatus=1;
     },
     error : err => {
       this.formStatus=2;
       this.errorMessage=err.error.error;
     }
   })
  }

 }
