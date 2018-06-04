import {environment} from '../../environments/environment';
import {UserService} from '../user/user.service';
import {AlertDefinition} from './alert-definition';
import {AlertDefinitionParameter} from './alert-definition-params';
import {AlertDefinitionParameterView} from './alert-definition-params-view';
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
//    return this.http.get<AlertDefinition[]>(this.baseUrl + '/user/' + userId);

    //      http://localhost:8090/api/config/definition/user/{user_id}
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

  mapParamToView(param: AlertDefinitionParameter): AlertDefinitionParameterView {
    const view = new AlertDefinitionParameterView();
    view.id = param.id;
    view.limit = param.parameterLimit;
    view.unit = param.unit;
    switch (param.parameterName) {
      case 'TEMP':
        view.name = 'Temperature';
        view.unit = 'C';
        break;
      case 'WIND':
        view.name = 'Wind';
        view.unit = 'm/s';
        break;
      case 'RAIN':
        view.name = 'Rain';
        view.unit = 'mm';
        break;
      case 'HUMI':
        view.name = 'Humidity';
        view.unit = '%';
        break;
      case 'PRES':
        view.name = 'Pressure';
        view.unit = 'hPa';
        break;
      case 'CLOU':
        view.name = 'Cloudy';
        view.unit = '%';
        break;
      default:
        view.name = param.parameterName;
    }
    switch (param.comparisonType) {
      case 2:
        view.type = '>';
        break;
      default:
        view.type = '<';
    }
    return view;
  }

  mapViewToParam(view: AlertDefinitionParameterView): AlertDefinitionParameter {
    const param = new AlertDefinitionParameter();
    param.id = view.id;
    param.parameterLimit = Number(view.limit);
    param.unit = view.unit;

    switch (view.name) {
      case 'Temperature':
        param.parameterName = 'TEMP';
        break;
      case 'Wind':
        param.parameterName = 'WIND';
        break;
      case 'Rain':
        param.parameterName = 'RAIN';
        break;
      case 'Humidity':
        param.parameterName = 'HUMI';
        break;
      case 'Pressure':
        param.parameterName = 'PRES';
        break;
      case 'Cloudy':
        param.parameterName = 'CLOU';
        break;
      default:
        param.parameterName = '';
    }

    switch (view.type) {
      case '>':
        param.comparisonType = 2;
        break;
      default:
        param.comparisonType = 1;
    }

    return param;
  }

}
