import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';
import {ProfileComponent} from './profile.component';
import {UserService} from '../user/user.service';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let userService: UserService;
  let spy;
  
  beforeEach(() => {
    
//    const service = jasmine.createSpyObj('UserService', ['saveUser']);
//    
//    getSaveResult = service.save.and.returnValue( of(null) );
//    

    TestBed.configureTestingModule({
      imports: [FormsModule],
      providers: [ProfileComponent, {provide: UserService, useClass: MockUserService}]
    });

    component = TestBed.get(ProfileComponent);
    userService = TestBed.get(UserService);
//    spy = spyOn(userService, 'saveUser').and.callThrough();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have user after Angular calls ngOnInit', () => {
    component.ngOnInit();
    expect(component.user.name).toBeTruthy();
  });
  
//    it('should call user service after data update', () => {
//    component.ngOnInit();
//    component.save();
//    
//    expect(spy).toHaveBeenCalled();
//  });

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

