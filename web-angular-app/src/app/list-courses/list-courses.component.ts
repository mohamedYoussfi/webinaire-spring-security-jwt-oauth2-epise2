import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {CourseService} from "../services/course.service";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-list-courses',
  templateUrl: './list-courses.component.html',
  styleUrls: ['./list-courses.component.css']
})
export class ListCoursesComponent implements OnInit {
  courses:any;

  constructor(private http:HttpClient, private router : Router, public courseService : CourseService, public authService : AuthService) {
  }
  ngOnInit(): void {
    this.courseService.allCourses().subscribe({
      next :value => {
        this.courses=value;
      },
      error:err => {}
    })
  }
}
