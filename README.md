# Description

This is a simple app that helps us understand the lifecycle of fragments and  transaction behaviour via **FragmentTransaction** api, including add, replace, show, hide, attach, detach and set primary navigation fragment.

# Lifecycle

Through transaction apis, you should find the followings:
- `add` is simply adding a fragment into a container. Since you might add multiple fragments into different containers, none of the fragments will go into pause, stop or destroyView routine.
- `replace` is used when we want to replace a fragment with another. Since replacement means the previous fragment is not needed, for now. Thus, the old fragment need to go through pause, stop and destroyView. However, it will not be detached or destroy, because we still need to keep the state of the old fragment. When we popBackStack, it will go through onViewCreated, onStart, onResume then displays it on screen.
- `hide`is used to hide a specific fragment that is currently in the backstack 
- `show` is used to show a fragment that was previously hidden.
- `detach` is detaching a specific fragment from the screen, but it will still remain in the backstack. This will make the fragment go through pause, stop, destroyView.
- `attach` is attaching a fragment that was previously detached. However, unlike `show`, `attach` will display the fragment on the screen. This function will make the fragment go through onViewCreated, onStart, onResume.
- `setPrimaryNavigationFragment` doesn't do mush on this sample. Because it is used to set a currently active fragment in this FragmentManager as the primary navigation fragment. The primary fragment will be called first to process delegated navigation actions such as `FragmentManager.popBackStack()`if no ID or transaction name is provided to pop to.

If there is any bug or suggestions, feel free to send an issue or push your suggested fix.

Thanks and Enjoy.
