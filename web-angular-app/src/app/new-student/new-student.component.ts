import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-new-student',
  templateUrl: './new-student.component.html',
  styleUrls: ['./new-student.component.css']
})
export class NewStudentComponent implements OnInit{
  studentFormGroup!:FormGroup
  fileSource! : File;
 constructor(private fb : FormBuilder, private http:HttpClient) {
 }
 ngOnInit() {
    this.studentFormGroup=this.fb.group({
      name : this.fb.control(''),
      birthDate : this.fb.control(''),
      gender : this.fb.control(''),
    })
 }

  saveNewStudent() {
    let formData=new FormData();
    formData.append('studentData', JSON.stringify(this.studentFormGroup.value));
    formData.append('photoFile', this.fileSource);
    this.http.post("http://localhost:8087/students", formData)
      .subscribe({
        next: value => {
          alert('Uploaded Successfully.');
        },
        error: error => {
          alert(error.message);
        }
      });
  }

  selectFile(event: any) {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.fileSource=file;
    }
  }
}
