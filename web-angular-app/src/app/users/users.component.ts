import { Component } from '@angular/core';
import {AuthService} from "../services/auth.service";
import {Observable} from "rxjs";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent {
  users: any;
  roles!: Observable<any>;
  keyWord: any;
  constructor(private authService : AuthService) {
  }
  ngOnInit() {
    this.getAllUsers();
  }

  private getAllUsers() {
    this.authService.getAllUsers().subscribe({
      next : value => {
        this.users=value;
        this.getRoles();
      },
      error : err =>{
        console.log(err);
      }
    });
  }
  userRoles(u: any) {

  }

  private getRoles() {
    this.roles=this.authService.getRoles();
  }

  addRoleToUser(user: any, roleName : string, deleteRequestRole : boolean) {
   this.authService.addRoleToUser({username :user.username, roleName : roleName, deleteRequestRole:deleteRequestRole})
     .subscribe({
       next : value => {
         user.appRoles.push(value);
         if(deleteRequestRole) {
           let index=user.requestedRoles.indexOf(roleName);
           user.requestedRoles.splice(index,1);
         }
       },
       error : err => {
        alert(err.error.error);
       }
     })
  }

  removeRoleFromUser(user: any, role : any) {
    this.authService.removeRoleFromUser({username : user.username, roleName : role.roleName})
      .subscribe({
        next : value => {
          let index = user.appRoles.indexOf(role);
          user.appRoles.splice(index,1);
        },
        error : err => {
          alert(err.error.error);
        }
      })
  }

  activateAccount(user : any) {
    let value:boolean=!(user.status=='ACTIVATED');
    this.authService.activateAccount(value,user.id).subscribe({
      next : resp => {
        user.status=resp.status;
      },
      error : err => {
        alert(err.error.errorMessage);
      }
    })
  }

  searchUsers() {
    this.authService.searchUsers(this.keyWord).subscribe({
      next : value => {
        this.users=value;
      },
      error : err =>{
        console.log(err);
      }
    });
  }
}
