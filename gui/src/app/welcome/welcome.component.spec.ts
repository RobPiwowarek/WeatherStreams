import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';
import {UserService} from '../user/user.service';
import {WelcomeComponent} from './welcome.component';
import {Router} from '@angular/router';

describe('WelcomeComponent', () => {
  let component: WelcomeComponent;
  let fixture: ComponentFixture<WelcomeComponent>;
  let userService: UserService;

  beforeEach(() => {
    
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    
    TestBed.configureTestingModule({
      imports: [FormsModule],
      providers: [WelcomeComponent, 
                  {provide: UserService, useClass: MockUserService},
                  {provide: Router, useValue: routerSpy}]
    });

    component = TestBed.get(WelcomeComponent);
    userService = TestBed.get(UserService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});


class MockUserService {
  public currentUser = {
    id: 1,
    username: 'Test@test.com',
    name: 'Test',
    surname: 'Some',
    slack: 'myslack'
  };
}