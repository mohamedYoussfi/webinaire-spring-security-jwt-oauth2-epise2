import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {CourseService} from "../services/course.service";

@Component({
  selector: 'app-new-course',
  templateUrl: './new-course.component.html',
  styleUrls: ['./new-course.component.css']
})
export class NewCourseComponent implements OnInit{
  newCourseFormGroup! : FormGroup;
  file! : File;
  constructor(private fb: FormBuilder, private courseService : CourseService) {
  }
  ngOnInit() {
    this.newCourseFormGroup=this.fb.group({
      title : this.fb.control(''),
      groupName : this.fb.control('')
    });
  }

  addNewCourse() {
    let courseData=JSON.stringify(this.newCourseFormGroup.value);
    this.courseService.saveNewCourse(courseData,this.file).subscribe({
      next : value => {
        alert("success");
      },
      error : err => {
        alert(err);
      }
    });
  }

  selectFile(event:any) {
    if(event.target.files.length>0) {
      this.file = event.target.files[0];
    }
  }
}
