import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import { Observable } from 'rxjs';
import {AuthService} from "../services/auth.service";

@Injectable({
  providedIn: 'root'
})
export class AuthorizationByRolesGuard implements CanActivateChild {
  constructor(private authService:AuthService, private router : Router) {
  }
  canActivateChild(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if(!this.authService.isAuthenticated()){
      this.router.navigateByUrl("/login");
      return false;
    } else {
      let routeRoles=route.data['roles'];
      if(routeRoles=="*") return true;
      let authorized:boolean=false;
      for (let role of routeRoles){
        let hasRole:boolean=this.authService.hasRole(role);
        if(hasRole){
          authorized=true;
          break;
        }
      }
      if(authorized==false) alert("Not Authorized");
      return authorized;
    }
  }

}
