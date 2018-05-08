import {AlertDefinition} from './alert-definition';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';

@Injectable()
export class AlertConfigService {

 constructor(private http: HttpClient) { }
  public getAlertDefinitions(): Observable<AlertDefinition[]> {

    return this.http.get<AlertDefinition[]>('assets/alert-definitions.json');
  }

}
