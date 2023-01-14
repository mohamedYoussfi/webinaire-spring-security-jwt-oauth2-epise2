import {AbstractControl, AsyncValidator, ValidationErrors} from "@angular/forms";
import {map, Observable} from "rxjs";
import {AuthService} from "../services/auth.service";
import {Injectable} from "@angular/core";
@Injectable()
export class UniqueUserValidator implements AsyncValidator{
  constructor(private authService : AuthService) {
  }
  validate(control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
    let username = control.value;
    return this.authService.isUsernameAvailable(username).pipe(
      map((result)=>{
        if (result==true) return null
        else return {usernameDuplicated : true}
      })
    );
  }

}
