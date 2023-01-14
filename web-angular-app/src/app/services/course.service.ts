import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private classRoomHost="http://localhost:8888/classroom-service";
  constructor(private http:HttpClient) { }
  public saveNewCourse(courseData : string,file : File){
    let formData=new FormData();
    formData.append('file', file);
    formData.append("courseData",courseData);
    return this.http.post(this.classRoomHost+"/courses/", formData);
  }

  public getStudent(studentId:string) {
    return this.http.get(this.classRoomHost+"/students/"+studentId);
  }

  public uploadCoursePhoto(file : File, courseId: string) {
    let data=new FormData();
    data.append("photoFile",file);
    return this.http.put<any>(this.classRoomHost+"/courses/photo/"+courseId,data);
  }

  addNewSubscription(subscription: { userId: string | undefined; courseId: string }) {
    return this.http.post<any>(this.classRoomHost+"/courses/subscription",subscription);
  }

  getStudentSubscriptions() {
    return this.http.get<any>(this.classRoomHost+"/courses/studentSubscriptions");
  }

  getCourseSubscriptions(courseId :string) {
    return  this.http.get<any>(this.classRoomHost+"/courses/courseSubscriptions/"+courseId);
  }
  myCourses() {
    return this.http.get(this.classRoomHost+"/myCourses")
  }
  allCourses() {
    return this.http.get(this.classRoomHost+"/courses")
  }

  getCourseDetails(courseId: string) {
    return this.http.get(this.classRoomHost+"/courses/"+courseId)
  }
}
