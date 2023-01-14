import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeacherTemplateComponent } from './teacher-template.component';

describe('TeacherTemplateComponent', () => {
  let component: TeacherTemplateComponent;
  let fixture: ComponentFixture<TeacherTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TeacherTemplateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TeacherTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
