import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {CourseService} from "../services/course.service";

@Component({
  selector: 'app-course-subscription',
  templateUrl: './course-subscription.component.html',
  styleUrls: ['./course-subscription.component.css']
})
export class CourseSubscriptionComponent implements OnInit{
  courseId!: string;
  subscriptions: any;

  ngOnInit() {
    this.getSubscriptions();
  }

  constructor(private authService : AuthService, private courseService : CourseService) {
  }
  addNewSubscription() {
    let subscription={userId:this.authService.userProfile?.userId, courseId:this.courseId}
    this.courseService.addNewSubscription(subscription).subscribe({
      next : value => {
       this.subscriptions.push(value);
      },
      error : err => {
        alert("Error");
      }
    })
  }

  private getSubscriptions() {
    this.courseService.getStudentSubscriptions().subscribe({
        next: value => {
          this.subscriptions = value;
        }, error: err => {
          alert("error");
        }
      }
    );
  }

}
