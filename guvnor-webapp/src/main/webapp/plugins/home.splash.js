$registerSplashScreen({
    id: 'home.splash',
    templateUrl: "home.splash.html",
    title: function () {
        return 'Welcome';
    },
    display_next_time: true,
    interception_points: ['org.guvnor.DefaultPerspective']
});