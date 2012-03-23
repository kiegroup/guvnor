The main purpose of this example is to show how to build a Guvnor like web application using GWTP and Ballroom.

Notes:

1. Place and history management
Please check  ClientPlaceManager.java and NameTokens.java for Place and history management related code.

You need a @NameToken annotation on the proxy class. This way the GWTP place manager knowS which proxy to invoke 
when a PlaceRequest comes in. Below is an example of PlaceRequest:

                PlaceRequest placeRequest = new PlaceRequest(NameTokens.helloWorld);
                placeRequest = placeRequest.with("tabName", constants.helloWorld());
                placeManager.revealPlace(placeRequest);

PlaceRequest can take parameters. In this example, we pass in tab names with a "tabName" parameter in PlaceRequest.


2. MVP and ActivityManager 
Check AdminAreaPresenter.java and AdminAreaView.java for a typical Presenter-Proxy-View triplet MVP pattern used in GWTP. 
In most cases, we do not need to write our own Proxy class, it can be created automatically by GWTP code gen. 

Proxies are lightweight classes allowing code splitting and lazily instantiating presenters. Essentially proxies are the lightweight 
"asleep" state of the presenter. When a presenter is "asleep", the proxy listens to any event that would require their associated 
presenter and view to be created or revealed.


GWTP does support multiple activities. 


3. @ContentSlot
GWTP use @ContentSlot annotation to decouple presenters from the "presenter container" (ie. presenters that have nested presenters). 
Check PerspectivesPanelPresenter.java and AdminAreaPresenter.java for how this is done. Basically, AdminAreaPresenter sends out a 
RevealContentEvent.fire(this, PerspectivesPanelPresenter.TYPE_MainContent, this) event to notify it wants itselfs to be revealed in 
a "presenter container" that supports TYPE_MainContent.


4. EventBus

4.1 Custom event
Check RefreshAdminAreaEvent.java for a typical implementation of custom Event class. 

4.2 Fire event:
As all GWTP Presenters implement HasHandlers by default, the most convenient way to fire an event is to call Event's static fire method:
public static void fire(HasHandlers source) , eg:

    private void fireRefreshEvent() {
    	RefreshAdminAreaEvent.fire(this);
    }

4.3
Register event handler:
    @Override
    protected void onBind() {
        super.onBind();
        addRegisteredHandler(RefreshAdminAreaEvent.getType(),
                new RefreshAdminAreaHandler() {
                    @Override
                    public void onRefreshAdminArea(RefreshAdminAreaEvent event) {
                        //refresh the view
                    }
                }
        );
    }

The addRegisteredHandler method makes sure that every registered handler is correctly unregistered when the presenter is unbound. 

NOTE: The customer event class can be replaced by GWTP's boilerplate codegen:  
http://code.google.com/p/gwt-platform/wiki/BoilerplateGeneration#Generate_Event_and_Event_Handler


5. Presenter has following lifecycles. This provides possibilities to hooks custom actions. 

    onBind() 
    onUnbind() 
    onReveal() 
    onHide()
    onReset() 


6. Gatekeeper is used in GWTP to do client side role-based authorization. 

public class IsAdminGatekeeper implements Gatekeeper {
    private final CurrentUser currentUser;

    @Inject
    public IsAdminGatekeeper(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public boolean canReveal() {
        return currentUser.isAdmin();
    }
}

An annotation is used on Proxy to mark that this presenter can only be displayed to admin type user:

  @ProxyCodeSplit
  @NameToken(NameTokens.adminPage)
  @UseGatekeeper(IsAdminGatekeeper.class)
  public interface MyProxy extends TabContentProxyPlace<AdminAreaPresenter> {
  }



7. Tabs
There are some limitations with GWTP's tab implementation. For example, the tab can not be dynamically added. Also presenter is singleton, 
we need to use PresenterWidget or write our own proxy implementation to allow having multiple instances of presenters. 
http://code.google.com/p/gwt-platform/wiki/FrequentlyAskedQuestions#When_should_I_use_a_Presenter,_PresenterWidget_or_a_regular_plai