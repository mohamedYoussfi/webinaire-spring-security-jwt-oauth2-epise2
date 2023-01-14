import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseSubscriptionComponent } from './course-subscription.component';

describe('CourseSubscriptionComponent', () => {
  let component: CourseSubscriptionComponent;
  let fixture: ComponentFixture<CourseSubscriptionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CourseSubscriptionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CourseSubscriptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
