import {AlertDefinitionParameter} from './alert-definition-params';
import {AlertConfigService} from './alert-config.service';
import {AlertDefinition} from './alert-definition';
import {AlertDefinitionParameterView} from './alert-definition-params-view';
import {Component, OnInit} from '@angular/core';
import {SelectItem} from 'primeng/api';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {

  displayDialog: boolean;
  definition: AlertDefinition = new AlertDefinition();
  selectedDefinition: AlertDefinition;
  newDefinition: boolean;
  definitions: AlertDefinition[];
  cols: any[];
  parameters: AlertDefinitionParameterView[];
  effects: SelectItem[];
  signs: SelectItem[];

  constructor(private alertConfigService: AlertConfigService) {}

  ngOnInit() {
    this.cols = [
      {field: 'alertName', header: 'Name'},
      {field: 'location', header: 'Location'},
      {field: 'duration', header: 'Duration'},
      {field: 'active', header: 'Active'},
      {field: 'emailNotif', header: 'Email'},
      {field: 'slackNotif', header: 'Slack'},
      {field: 'timestamp', header: 'Timestamp'}
    ];
    this.effects = [
      {label: 'Rain', value: 'Rain'},
      {label: 'Wind', value: 'Wind'},
      {label: 'Temperature', value: 'Temperature'},
      {label: 'Humidity', value: 'Humidity'}
    ];
    this.signs = [
      {label: '>', value: 1},
      {label: '<', value: 2}
    ];

    this.alertConfigService.getAlertDefinitions().subscribe(
      d => this.definitions = d,
      e => console.log(e));

  }

  showDialogToAdd() {
    this.newDefinition = true;
    this.definition = new AlertDefinition();
    this.displayDialog = true;
  }

  save() {
    // TODO first update database
 
    this.definition.parameters = this.parameters.map(p => this.mapViewToParam(p));

    const defs = [...this.definitions];
    if (this.newDefinition) {
      defs.push(this.definition);
    } else {
      defs[this.definitions.indexOf(this.selectedDefinition)] = this.definition;
    }

    this.definitions = defs;
    this.definition = null;
    this.parameters = null;
    this.displayDialog = false;
  }

  delete() {
    const index = this.definitions.indexOf(this.selectedDefinition);
    this.definitions = this.definitions.filter((val, i) => i !== index);
    this.definition = null;
    this.displayDialog = false;
  }

  onRowSelect(event) {
    this.newDefinition = false;
    this.definition = this.cloneDefinition(event.data);
    this.displayDialog = true;
    this.parameters = this.definition.parameters.map(p => this.mapParamToView(p));
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
      default:
        view.name = '';
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

  cloneDefinition(d: AlertDefinition): AlertDefinition {
    let definition = new AlertDefinition;
    for (let prop in d) {
      definition[prop] = d[prop];
    }
    return definition;
  }

  public addParamRow() {
    const param = new AlertDefinitionParameterView();
    param.name = '';
    param.type = '>';
    param.limit = 0;

    this.parameters.push(param);
  }

  public deleteParam(selectedParam: AlertDefinitionParameterView) {
    const index = this.parameters.indexOf(selectedParam);
    this.parameters = this.parameters.filter((val, i) => i !== index);
  }

}
