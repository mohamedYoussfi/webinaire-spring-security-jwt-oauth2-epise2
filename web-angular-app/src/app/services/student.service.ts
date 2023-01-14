import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private http:HttpClient) { }
  public updatePhotoProfile(studentId :string,file : File){
    let formData=new FormData();
    formData.append('photoFile', file);
    return this.http.put("http://localhost:8088/students/"+studentId+"/profile", formData);
  }

  public getStudent(studentId:string) {
    return this.http.get("http://localhost:8088/students/"+studentId);
  }

}
