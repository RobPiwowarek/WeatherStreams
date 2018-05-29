import { AlertService } from './alert/alert-service';
import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ButtonModule} from 'primeng/button';
import {TableModule} from 'primeng/table';
import {InputTextModule} from 'primeng/inputtext';
import {HttpClientModule} from '@angular/common/http';
import {RouterModule} from '@angular/router';
import {MenuModule} from 'primeng/menu';
import {MenubarModule} from 'primeng/menubar';
import {KeyFilterModule} from 'primeng/keyfilter';
import {MultiSelectModule} from 'primeng/multiselect';
import {DropdownModule} from 'primeng/dropdown';
import {FormsModule} from '@angular/forms';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {MessagesModule} from 'primeng/messages';
import {MessageModule} from 'primeng/message';
import {ToolbarModule} from 'primeng/toolbar';
import {CheckboxModule} from 'primeng/checkbox';
import {DialogModule} from 'primeng/dialog';
import {InputSwitchModule} from 'primeng/inputswitch';
import {AutoCompleteModule} from 'primeng/autocomplete';
import {AppComponent} from './app.component';
import {WelcomeComponent} from './welcome/welcome.component';
import {UserService} from './user/user.service';
import {AlertComponent} from './alert/alert.component';
import { AlertConfigService } from './config/alert-config.service';
import {ConfigComponent} from './config/config.component';
import { ProfileComponent } from './profile/profile.component';

@NgModule({
  declarations: [
    AppComponent,
    WelcomeComponent,
    AlertComponent,
    ConfigComponent,
    ProfileComponent
  ],
  imports: [
    TableModule,
    BrowserModule,
    BrowserAnimationsModule,
    ButtonModule,
    InputTextModule,
    KeyFilterModule,
    DropdownModule,
    MessagesModule,
    MessageModule,
    InputSwitchModule,
    FormsModule,
    ToolbarModule,
    CheckboxModule,
    DialogModule,
    AutoCompleteModule,
    HttpClientModule,
    MenuModule,
    MenubarModule,
    RouterModule.forRoot([
      {path: 'alert', component: AlertComponent},
      {path: 'config', component: ConfigComponent},
      {path: 'profile', component: ProfileComponent},
      {path: '', redirectTo: '/', pathMatch: 'full'},
      {path: '**', redirectTo: '/', pathMatch: 'full'}
    ])
  ],
  providers: [
    UserService,
    AlertConfigService,
    AlertService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}

