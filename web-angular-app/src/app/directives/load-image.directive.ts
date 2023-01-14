import {Directive, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {subscribeOn} from "rxjs";
import {DomSanitizer} from "@angular/platform-browser";

@Directive({
  selector: '[appLoadImage]'
})
export class LoadImageDirective implements OnInit{
  @Output() onCreate : EventEmitter<any> =new EventEmitter<any>();
  @Input() item : any;
  @Input() resourceURL : any;
  constructor(private http : HttpClient, private domSanitize:DomSanitizer) {
  }
  ngOnInit(): void {
    this.http.get(this.resourceURL, {responseType: 'blob'})
      .subscribe({
        next: value => {
          this.item.imageURL=this.domSanitize.bypassSecurityTrustUrl(URL.createObjectURL(value));
        }
      });
  }
}
