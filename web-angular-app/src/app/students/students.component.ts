import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Router} from "@angular/router";

@Component({
  selector: 'app-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css']
})
export class StudentsComponent implements OnInit{
  students:any;
  constructor(private http:HttpClient, private router : Router) {
  }
  ngOnInit(): void {
    this.http.get("http://localhost:8087/students").subscribe({
      next :value => {
        this.students=value;
      },
      error:err => {}
    })
  }

  handleStudentDetail(st: any) {
    this.router.navigateByUrl("/admin/student-details/"+st.id)
  }
}
