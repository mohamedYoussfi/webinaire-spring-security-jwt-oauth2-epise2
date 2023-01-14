import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {Observable, of} from "rxjs";
import {StudentService} from "../services/student.service";

@Component({
  selector: 'app-student-details',
  templateUrl: './student-details.component.html',
  styleUrls: ['./student-details.component.css']
})
export class StudentDetailsComponent implements OnInit{
  studentId!:string;
  student : any;
  file! : File;
  timestamp :number=new Date().getTime();
  mode :number=0;
  materialLabel! :string;
  materialFile! : File;
  constructor(private http:HttpClient, private route : ActivatedRoute, private router : Router,
              private studentService : StudentService ) {
    this.studentId=this.route.snapshot.params['id'];
  }
  ngOnInit(): void {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.loadStudent();
  }

  selectFile(event: any) {
    if(event.target.files.length>0){
      let file=event.target.files[0];
      this.file=file;
    }
  }

  updatePhotoProfile() {
    this.studentService.updatePhotoProfile(this.studentId,this.file).subscribe({
      next : value => {
        this.router.navigated = false;
        this.router.navigate([this.router.url]);
        this.timestamp=new Date().getTime();
        this.mode=0;
      }
    })
  }

  private loadStudent() {
    this.studentService.getStudent(this.studentId)
      .subscribe({
        next : value => {
          this.student=value;
        },
        error : err => {
        }
      })
  }

  selectMaterialFile(event: any) {
    if(event.target.files.length>0){
      this.materialFile=event.target.files[0];
    }
  }

  addMaterial() {
    let formData=new FormData();
    formData.append('materialData', JSON.stringify({studentId:this.studentId,label:this.materialLabel}));
    formData.append('materialFile', this.materialFile);
    this.http.post("http://localhost:8087/materials", formData)
      .subscribe({
        next: value => {
          this.router.navigated = false;
          this.router.navigate([this.router.url]);
        },
        error: error => {
          alert(error.message);
        }
      });
  }
}
