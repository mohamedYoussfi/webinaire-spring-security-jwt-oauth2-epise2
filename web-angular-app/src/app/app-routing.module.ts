import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {StudentsComponent} from "./students/students.component";
import {StudentDetailsComponent} from "./student-details/student-details.component";
import {NewStudentComponent} from "./new-student/new-student.component";
import {LoginComponent} from "./login/login.component";
import {AdminTemplateComponent} from "./templates/admin-template/admin-template.component";
import {AuthorizationGuard} from "./guards/authorization.guard";
import {CoursesComponent} from "./courses/courses.component";
import {ProfileComponent} from "./profile/profile.component";
import {NewCourseComponent} from "./new-course/new-course.component";
import {RegistrationComponent} from "./registration/registration.component";
import {StudentTemplateComponent} from "./templates/student-template/student-template.component";
import {TeacherTemplateComponent} from "./templates/teacher-template/teacher-template.component";
import {ForgotPasswordComponent} from "./forgot-password/forgot-password.component";
import {RolesComponent} from "./roles/roles.component";
import {UsersComponent} from "./users/users.component";
import {AuthorizationByRolesGuard} from "./guards/authorization-by-roles.guard";
import {CourseSubscriptionComponent} from "./course-subscription/course-subscription.component";
import {ListCoursesComponent} from "./list-courses/list-courses.component";
import {MyCoursesComponent} from "./my-courses/my-courses.component";
import {CourseDetailsComponent} from "./course-details/course-details.component";

const routes: Routes = [
  {path : "", redirectTo : "login", pathMatch:"full"},
  {path : "login", component : LoginComponent},
  {path : "registration", component : RegistrationComponent},
  {path : "forgotPassWord" , component : ForgotPasswordComponent},
  {path : "admin", component : AdminTemplateComponent, canActivate :[AuthorizationGuard], canActivateChild:[AuthorizationByRolesGuard], children : [
      {path : "roles", component : RolesComponent, data : {roles : ['ADMIN']}},
      {path : "users", component : UsersComponent , data : {roles : ['ADMIN']}},
      {path : "new-user", component : RegistrationComponent , data : {roles : ['ADMIN']}},
      {path : "students", component : StudentsComponent, data : {roles : ['TEACHER']}},
      {path : "student-details/:id", component : StudentDetailsComponent, data : {roles : ['TEACHER']}},
      {path : "new-student", component : NewStudentComponent, data : {roles : ['TEACHER']}},
      {path : "courses", component : CoursesComponent, data : {roles : ['USER']}},
      {path : "my-courses", component : MyCoursesComponent, data : {roles : ['USER']}},
      {path : "all-courses", component : ListCoursesComponent, data : {roles : ['USER']}},
      {path : "new-course", component : NewCourseComponent, data : {roles : ['USER']}},
      {path : "course-details/:id", component : CourseDetailsComponent, data : {roles : ['USER']}},
      {path : "subscription", component : CourseSubscriptionComponent, data : {roles : ['USER']}},
      {path : "profile", component : ProfileComponent, data : {roles : ['*']}},
    ]},
  {path : "student", component : StudentTemplateComponent, canActivate :[AuthorizationGuard], children : [
      {path : "profile", component : ProfileComponent},
    ]},
  {path : "teacher", component : TeacherTemplateComponent, canActivate :[AuthorizationGuard], children : [
      {path : "profile", component : ProfileComponent},
    ]},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
