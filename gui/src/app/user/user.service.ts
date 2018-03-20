import {User} from './user';
import {UserLogin} from './user-login';
import {HttpHeaders} from '@angular/common/http';
import {HttpClient} from '@angular/common/http';
import {Injectable, Output, EventEmitter} from '@angular/core';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class UserService {

  public errorMessage = '';
  private username = '';
  private loggedIn = false;
  private baseUrl = 'http://localhost:8080/api/user/';
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  @Output() logginSucceed: EventEmitter<boolean> = new EventEmitter();

  constructor(protected httpClient: HttpClient) {}

  login(user, password) {
    this.errorMessage = '';
    if (user) {
      this.sendLogin(user, password).subscribe(
        u => this.processLogin(u),
        error => this.processLoginError(error)
      );
    } else {
      this.username = 'anonim'; // mock for test
      this.loggedIn = true;
      this.logginSucceed.emit(true);
    }
  }

  processLogin(user: User) {
    this.username = user.usename;
    this.loggedIn = true;
    this.logginSucceed.emit(true);
  }

  processLoginError(error) {
    console.log(error);
    this.errorMessage = 'Wrong username or password';
    this.logginSucceed.emit(false);
  }

  logout(): void {
    this.username = '';
    this.loggedIn = false;
  }

  isLogged(): boolean {
    return this.loggedIn;
  }

  getUsername(): string {
    return this.username;
  }

  public sendLogin(user, password): Observable<User> {
    const userLogin = new UserLogin();
    userLogin.login = user;
    userLogin.password = password;
    return this.httpClient.post<User>(this.baseUrl + 'login', userLogin, this.httpOptions);
  }
}
