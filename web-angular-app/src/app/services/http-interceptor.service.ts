import { HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import {AuthService} from "./auth.service";
const TOKEN_HEADER_KEY = 'Authorization';
@Injectable()
export class JwtInterceptorService implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  constructor(private authService: AuthService) { }
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<Object>> {
    let authReq = req;
    let url=req.url;
    const token = this.authService.userProfile?.accessToken;
    if (token != null && !url.endsWith("?rt") && !url.includes("/public")) {
      authReq = this.addTokenHeader(req, token);
    }
    return next.handle(authReq).pipe(catchError(error => {
      if (error instanceof HttpErrorResponse && !authReq.url.includes('/public') && error.status === 401) {
        console.log("Refresh Token Request ....");
        return this.handleRefreshToken(authReq, next);
      }
      return throwError(error);
    }));
  }
  private handleRefreshToken(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);
      const refreshToken = this.authService.userProfile?.refreshToken;
      const accessToken = this.authService.userProfile?.accessToken;
      const photoURL=this.authService.userProfile!.photoURL;
      if (refreshToken)
        return this.authService.refreshToken(refreshToken).pipe(
          switchMap((token: any) => {
            this.isRefreshing = false;
            this.authService.authenticateUser(token,photoURL);
            //console.log(token);
            this.refreshTokenSubject.next(accessToken);
            return next.handle(this.addTokenHeader(request, ''+this.authService.userProfile?.accessToken));
          }),
          catchError((err) => {
            console.log(err);
            this.isRefreshing = false;
            this.authService.logout();
            return throwError(err);
          })
        );
    }
    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap((token) => next.handle(this.addTokenHeader(request, token)))
    );
  }
  private addTokenHeader(request: HttpRequest<any>, token: string) {
    return request.clone({ headers: request.headers.set(TOKEN_HEADER_KEY, "Bearer "+token) });
  }
}
