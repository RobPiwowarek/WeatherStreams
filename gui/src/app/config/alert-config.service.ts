import {environment} from '../../environments/environment';
import { UserService } from '../user/user.service';
import {AlertDefinition} from './alert-definition';
import {HttpHeaders} from '@angular/common/http';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

@Injectable()
export class AlertConfigService {

  private baseUrl = 'http://localhost:8090/api/config/definition';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient, private userService: UserService) {}

  public getAlertDefinitions(): Observable<AlertDefinition[]> {
    const userId = this.userService.currentUser.id;
    return this.http.get<AlertDefinition[]>('assets/alert-definitions.json');
    //  http://localhost:8090/api/config/definition/user/{user_id}
  }

  public createAlertDefinition(definition): Observable<AlertDefinition> {
    return this.http.put<AlertDefinition>(this.baseUrl, definition, this.httpOptions);
  }

  public saveAlertDefinition(definition): Observable<void> {
    return this.http.post<void>(this.baseUrl, definition, this.httpOptions);
  }

  public deleteAlertDefinition(id): Observable<void> {
    return this.http.delete<void>(this.baseUrl + '/' + id);
  }

}
