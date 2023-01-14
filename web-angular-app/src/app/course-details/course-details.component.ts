import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {CourseService} from "../services/course.service";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrls: ['./course-details.component.css']
})
export class CourseDetailsComponent implements OnInit{
  courseId! : string;
  courses:any;
  currentCourse : any;
  mode: number=0;
  file: any;
  subscriptions: any;
  timestamp: number=0;
  constructor(route : ActivatedRoute, private router : Router, public courseService : CourseService, public authService : AuthService) {
    this.courseId=route.snapshot.params['id'];
  }
  ngOnInit() {
    this.getCourseDetails();
  }
  private getCourseDetails() {
    this.courseService.getCourseDetails(this.courseId).subscribe({
      next : value => {
        this.currentCourse=value;
      }, error :err => {
        alert("error");
      }
    })
  }

  handleStudentDetail(st: any) {
    this.router.navigateByUrl("/admin/student-details/"+st.id)
  }

  getCourseSubscriptions() {
    this.courseService.getCourseSubscriptions(this.courseId).subscribe({
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
        this.timestamp=new Date().getTime();
        this.mode=0
      }
    });;
  }

  getCourseResources() {

  }

  newResource() {

  }

  newHomework() {

  }

  sendEmailToStudents() {

  }
}
