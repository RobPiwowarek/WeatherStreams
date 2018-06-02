
import {UserService} from '../user/user.service';
import {Alert} from './alert';
import {AlertHistoryParam} from './alert-history-param';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

@Injectable()
export class AlertService {

  private baseUrl = 'http://localhost:8090/api/alert';
  constructor(private http: HttpClient, private userService: UserService) {}

  public getAlerts(): Observable<Alert[]> {
    const userId = this.userService.currentUser.id;
//        return this.http.get<Alert[]>('assets/alerts.json');
    return this.http.get<Alert[]>(this.baseUrl + '/user/' + userId);
  }

  public getAlertHitory(alertId): Observable<AlertHistoryParam[]> {
//        return this.http.get<AlertHistoryParam[]>('assets/alert-history.json');
    return this.http.get<AlertHistoryParam[]>(this.baseUrl + '/' + alertId + '/history');
  }

  public deleteAlert(id): Observable<void> {
    return this.http.delete<void>(this.baseUrl + '/' + id);
  }
}
