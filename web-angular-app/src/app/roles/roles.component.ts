import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-roles',
  templateUrl: './roles.component.html',
  styleUrls: ['./roles.component.css']
})
export class RolesComponent implements OnInit{
  roles: any;
  roleName: any;
 constructor(private authService : AuthService) {
 }
  ngOnInit() {
   this.getAllRoles();
  }

  private getAllRoles() {
    this.authService.getRoles().subscribe({
      next : value => {
        this.roles=value;
      },
      error : err =>{
        console.log(err);
      }
    });
  }

  saveRole() {
    this.authService.addNewRole({roleName:this.roleName}).subscribe({
      next : value => {
        this.roles.push(value);
      },
      error : err => {
        console.log(err);
      }
    });
  }

  deleteRole(r: any) {
    this.authService.deleteRole(r).subscribe({
      next : value => {
        let index = this.roles.indexOf(r);
        this.roles.splice(index,1);
      }, error : err => {
        alert("This role can not be deleted");
      }
    });
  }
}
