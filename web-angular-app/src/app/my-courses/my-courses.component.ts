import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {CourseService} from "../services/course.service";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-my-courses',
  templateUrl: './my-courses.component.html',
  styleUrls: ['./my-courses.component.css']
})
export class MyCoursesComponent implements OnInit{
  courses:any;

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

  handleCourseDetails(course: any) {
    this.router.navigateByUrl("/admin/course-details/"+course.id);
  }
}
