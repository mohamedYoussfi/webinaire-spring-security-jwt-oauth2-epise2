import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {JwtHelperService} from "@auth0/angular-jwt";
import {UserProfile} from "../model/user-profile.model";
import {Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public host : string ="http://localhost:8888/auth-service";
  public userProfile : UserProfile | null =null;
  public ts:number=0;
  constructor(private http: HttpClient) {
    //this.loadProfile();
  }

  public login(username : string, password : string){
    let options= {
      headers: new HttpHeaders().set('Content-Type','application/x-www-form-urlencoded')
    }
    let params=new HttpParams()
      .set("grantType","password")
      .set('username',username)
      .set('password',password)
      .set('withRefreshToken',true)
    return this.http.post(this.host+"/public/auth", params,options);
  }
  public authenticateUser(idToken : any, photoURL:any){
    let jwtHelperService=new JwtHelperService();
    let accessToken=idToken['access-token'];
    let refreshToken = idToken['refresh-token'];
    let decodedJWTAccessToken = jwtHelperService.decodeToken(accessToken);
    this.userProfile= {
      userId : decodedJWTAccessToken.sub,
      username : decodedJWTAccessToken.username,
      email : decodedJWTAccessToken.email,
      scope : decodedJWTAccessToken.scope,
      accessToken : accessToken,
      refreshToken:refreshToken,
      photoURL : photoURL
    }
    localStorage.setItem('userProfile',JSON.stringify(this.userProfile));
  }
  public refreshToken(refreshToken:string){
    let options = {
      headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')
    };

    let params = new HttpParams()
      .set("grantType", "refreshToken")
      .set("refreshToken",this.userProfile!.refreshToken)
      .set('withRefreshToken',true)
    return this.http.post(this.host+"/public/auth?rt", params,options)
  }
  logout(){
    this.userProfile=null;
    localStorage.removeItem("userProfile");
    window.location.href="/"
  }

  isAuthenticated() {
    return this.userProfile!=null;
  }
  loadProfile(){
    let profile =localStorage.getItem("userProfile");
    if(profile==undefined) return;
    let item = JSON.parse(profile);
    this.authenticateUser({"access-token":item.accessToken,"refresh-token":item.refreshToken},item.photoURL);
  }

  loadUserProfile() {
    return this.http.get<any>(this.host+"/user/profile");
  }

  setCurrentUser(value: any) {
    this.userProfile!.photoURL=value.photoURL;
    localStorage.setItem('userProfile',JSON.stringify(this.userProfile));
  }

  savePhotoURL(photoURL:string) {
    this.userProfile!.photoURL=photoURL;
    this.ts=new Date().getTime();
  }

  public hasRole(role : string) :boolean{
    if(!this.userProfile) return false;
    console.log(this.userProfile.scope);
    console.log(role);
    console.log(this.userProfile.scope.includes(role));
    return this.userProfile.scope.includes(role);
  }
  public uploadPhotoProfile(file : File) {
    let data=new FormData();
    data.append("photoFile",file);
    return this.http.put<any>(this.host+"/user/profile",data);
  }

  registerUser(user :any) {
    let options= {
      headers: new HttpHeaders().set('Content-Type','application/json')
    }
    return this.http.post(this.host+"/public/register",user, options);
  }

  isUsernameAvailable(username: string) {
    return this.http.get<boolean>(this.host+"/public/isUsernameAvailable?username="+username);
  }

  forgotPassword(data :any, state : number) {
    let options = {
      headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')
    };

    if(state == 1) {
      let params = new HttpParams()
        .set("email", data.email)
      return this.http.post<any>(this.host+"/public/forgotPassword", params,options)
    } else if(state == 2){
      let params = new HttpParams()
        .set("email", data.email)
        .set("authorizationCode", data.secretCode)
      ;
      return this.http.post<any>(this.host+"/public/requestForPasswordInit", params,options)

    }
    else {
      let params = new HttpParams()
        .set("email", data.email)
        .set("password",data.password)
        .set("confirmPassword",data.confirmPassword)
        .set("authorizationCode", data.secretCode)
      ;
      return this.http.post<any>(this.host+"/public/passwordInitialization", params,options)
    }
  }

  getRoles() {
    return this.http.get(this.host+"/admin/roles");
  }

  addNewRole(role : any) {
    return this.http.post(this.host+"/admin/roles",role);
  }

  deleteRole(r: any) {
    return this.http.delete(this.host+"/admin/roles/"+r.id);
  }

  getAllUsers() {
    return this.http.get(this.host+"/admin/users");
  }

  addRoleToUser(data: any) {
    return this.http.post<any>(this.host+"/admin/addRoleToUser",data);
  }

  removeRoleFromUser(data: { roleName: string; username: string }) {
    return this.http.post<any>(this.host+"/admin/removeRoleFromUser",data);
  }

  changePassword(data: any) {
    return this.http.put(this.host+"/admin/changePassword", data);
  }

  isEmailAvailable(email: string) {
    return this.http.get<boolean>(this.host+"/public/isEmailAvailable?email="+email);
  }

  activateAccount(value: boolean, userId : string) {
    return this.http.put<any>(this.host+"/admin/activateAccount",{ userId: userId, value:value });
  }

  searchUsers(keyWord: any) {
    return this.http.get(this.host+"/admin/searchUsers?keyWord="+keyWord);
  }

  verifyEmail(userId:string) {
    return  this.http.put(this.host+"/admin/verifyEmail",{});
  }
}
