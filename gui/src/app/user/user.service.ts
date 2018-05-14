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
  private baseUrl = 'http://localhost:8090/api/user/';
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
        u => this.processLogin({id: 1, name: 'Anonymous', surname: 'P', username: 'anonim'}),
        error => this.processLoginError(error)
      );
    } else {
      this.currentUser = {id: 1, name: 'Anonymous', surname: 'P', username: 'anonim'};
      this.loggedIn = true;
      this.logginSucceed.emit(true);
    }
  }

  processLogin(user: User) {
    this.currentUser = user;
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

  public sendLogin(user, password): Observable<void> {
    const userLogin = new UserLogin();
    userLogin.username.value = user;
    userLogin.password.value = password;
    return this.httpClient.post<void>(this.baseUrl + 'login', userLogin, this.httpOptions);
  }
}
