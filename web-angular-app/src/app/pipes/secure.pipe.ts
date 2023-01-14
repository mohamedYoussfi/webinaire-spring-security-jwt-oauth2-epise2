import { Pipe, PipeTransform } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {map, Observable, of} from "rxjs";
@Pipe({
  name: 'secure'
})
export class SecurePipe implements PipeTransform {
  constructor(private http: HttpClient, private sanitizer: DomSanitizer) { }

  transform(url:string): Observable<SafeUrl> {
    if(url.startsWith("null")) return of("/assets/up.jpeg");
    return this.http
      .get(url, { responseType: 'blob' })
      .pipe(map(value => this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(value))));
}
}
