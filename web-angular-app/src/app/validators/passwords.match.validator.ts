import {FormGroup} from "@angular/forms";

export function valuesMatchValidator(controlName :string, matchingControlName:string){
  return (formGroup : FormGroup) => {
    let control = formGroup.controls[controlName];
    let matchingControl = formGroup.controls[matchingControlName];
    if(matchingControl.errors && ! matchingControl.errors['ValuesNotMatch'])
      return
    if(control.value != matchingControl.value){
      matchingControl.setErrors({ValuesNotMatch : true})
    } else {
      matchingControl.setErrors(null);
    }
  }
}
