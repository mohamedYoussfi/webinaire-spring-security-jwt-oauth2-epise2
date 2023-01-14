import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {CourseService} from "../services/course.service";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class CoursesComponent {
  courses:any;
  currentCourse : any;
  mode: number=0;
  file: any;
  subscriptions: any;
  constructor(private http:HttpClient, private router : Router, public courseService : CourseService, public authService : AuthService) {
  }
  ngOnInit(): void {
    this.courseService.myCourses().subscribe({
      next :value => {
        this.courses=value;
      },
      error:err => {}
    })
  }

  handleStudentDetail(st: any) {
    this.router.navigateByUrl("/admin/student-details/"+st.id)
  }

  setCurrentCourse(c: any) {
    this.currentCourse=c;
    this.courseService.getCourseSubscriptions(c.id).subscribe({
      next : value => {
        this.subscriptions=value;
      }, error :err => {
        alert("error");
      }
    })
  }

  selectFile(event: any) {
    if(event.target.files.length>0){
      this.file=event.target.files[0];
    }
  }

  uploadCoursePhoto() {
    this.courseService.uploadCoursePhoto(this.file, this.currentCourse.id).subscribe({
      next : value => {
        this.currentCourse.pictureURL=value.pictureURL;
      }
    });;
  }
}
