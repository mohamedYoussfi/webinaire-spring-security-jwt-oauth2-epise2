import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {Route, Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {valuesMatchValidator} from "../validators/passwords.match.validator";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit{
  profile : any;
  mode: number=0;
  photoFile!: File;
  public timeStamp: number=0;
  changePwFormGroup!: FormGroup;
  constructor(private authService : AuthService, private router : Router, private fb : FormBuilder) {
  }
  ngOnInit(): void {
    this.changePwFormGroup=this.fb.group({
      currentPassword : ['123456789', Validators.required],
      newPassword : ['', Validators.required],
      confirmPassword : ['', Validators.required],
    }, {validators : valuesMatchValidator('newPassword','confirmPassword')}
    );
    this.authService.loadUserProfile().subscribe({
      next : value => {
        this.profile=value;
      },
      error : err => {
        console.log(err);
      }
    })
  }

  selectFile(evt: any) {
    if(evt.target.files.length>0){
      this.photoFile=evt.target.files[0];
    }
  }

  uploadFile() {
    this.authService.uploadPhotoProfile(this.photoFile).subscribe({
      next : value => {
        this.router.navigated=true;
        this.router.navigate([this.router.url]);
        this.timeStamp=new Date().getTime();
        this.authService.ts=new Date().getTime();
        this.authService.savePhotoURL(value.photoURL);
        this.profile.photoURL=value.photoURL;
        this.mode=0;
      }
    });
  }
  changePassword() {
    this.authService.changePassword(this.changePwFormGroup.value).subscribe({
      next : value => {
        alert("The password has been changed successfully");
        this.authService.logout();
      },
      error : err => {
        alert(err.error.error);
      }
    });
  }

  verifyEmail(profile: any) {
    this.authService.verifyEmail(profile.userId).subscribe({
      next : value => {
        alert("Check Your email")
      }, error : err => {
        alert("Error");
      }
    });
  }
}
