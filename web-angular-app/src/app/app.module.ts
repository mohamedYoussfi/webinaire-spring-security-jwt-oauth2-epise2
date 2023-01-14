import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { StudentsComponent } from './students/students.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { LoadImageDirective } from './directives/load-image.directive';
import { StudentDetailsComponent } from './student-details/student-details.component';
import { NewStudentComponent } from './new-student/new-student.component';
import { LoginComponent } from './login/login.component';
import { AdminTemplateComponent } from './templates/admin-template/admin-template.component';
import {JwtInterceptorService} from "./services/http-interceptor.service";
import { SecurePipe } from './pipes/secure.pipe';
import { CoursesComponent } from './courses/courses.component';
import { ProfileComponent } from './profile/profile.component';
import { NewCourseComponent } from './new-course/new-course.component';
import { RegistrationComponent } from './registration/registration.component';
import { TeacherTemplateComponent } from './templates/teacher-template/teacher-template.component';
import { StudentTemplateComponent } from './templates/student-template/student-template.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { RolesComponent } from './roles/roles.component';
import { UsersComponent } from './users/users.component';
import { CourseSubscriptionComponent } from './course-subscription/course-subscription.component';
import { ListCoursesComponent } from './list-courses/list-courses.component';
import { MyCoursesComponent } from './my-courses/my-courses.component';
import { CourseDetailsComponent } from './course-details/course-details.component';

@NgModule({
  declarations: [
    AppComponent,
    NavBarComponent,
    StudentsComponent,
    LoadImageDirective,
    StudentDetailsComponent,
    NewStudentComponent,
    LoginComponent,
    AdminTemplateComponent,
    SecurePipe,
    CoursesComponent,
    ProfileComponent,
    NewCourseComponent,
    RegistrationComponent,
    TeacherTemplateComponent,
    StudentTemplateComponent,
    ForgotPasswordComponent,
    RolesComponent,
    UsersComponent,
    CourseSubscriptionComponent,
    ListCoursesComponent,
    MyCoursesComponent,
    CourseDetailsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule, HttpClientModule, ReactiveFormsModule, FormsModule
  ],
  providers: [
    {provide:HTTP_INTERCEPTORS, useClass:JwtInterceptorService, multi:true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
