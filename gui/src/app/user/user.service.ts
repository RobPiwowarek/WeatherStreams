import {User} from './user';
import {UserLogin} from './user-login';
import {HttpHeaders} from '@angular/common/http';
import {HttpClient} from '@angular/common/http';
import {Injectable, Output, EventEmitter} from '@angular/core';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class UserService {

  public errorMessage = '';
  public currentUser: User;
  private loggedIn = false;
  private baseUrl = 'http://localhost:8090/api/user';
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
      this.currentUser = {id: 1, username: 'anonim', name: 'ABC', surname: 'abc', slack: 'fake'};
      this.loggedIn = true;
      this.logginSucceed.emit(true);
    }
  }

  processLogin(user: User) {
    if (user) {
      this.currentUser = user;
    } else {
      this.currentUser = {id: 1, username: 'anonim', name: 'ABC', surname: 'abc', slack: 'fake'};
    }
    this.loggedIn = true;
    this.logginSucceed.emit(true);
  }

  processLoginError(error) {
    console.log(error);
    this.errorMessage = 'Wrong username or password';
    this.logginSucceed.emit(false);
  }

  logout(): void {
    this.currentUser = null;
    this.loggedIn = false;
  }

  isLogged(): boolean {
    return this.loggedIn;
  }

  getUsername(): string {
    return this.currentUser.username;
  }

  public sendLogin(user, password): Observable<User> {
    const userLogin = new UserLogin();
    userLogin.username = user;
    userLogin.password = password;
    return this.httpClient.post<User>(this.baseUrl + '/login', userLogin, this.httpOptions);
  }

  public saveUser(user: User): Observable<void> {
    this.currentUser = user;
        return new Observable<void>();
//    return this.httpClient.post<void>(this.baseUrl, user, this.httpOptions);
  }
}
