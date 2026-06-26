package com.within.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.within.app.billing.BillingManager;
import com.within.app.data.local.WithinDatabase;
import com.within.app.data.local.dao.DayProgressDao;
import com.within.app.data.local.dao.JournalEntryDao;
import com.within.app.data.local.dao.JourneyProgressDao;
import com.within.app.data.preferences.UserPreferences;
import com.within.app.data.repository.JourneyProgressRepository;
import com.within.app.data.repository.JourneyRepository;
import com.within.app.data.repository.MessageRepository;
import com.within.app.di.AppModule_ProvideGsonFactory;
import com.within.app.di.DatabaseModule_ProvideDayProgressDaoFactory;
import com.within.app.di.DatabaseModule_ProvideJournalEntryDaoFactory;
import com.within.app.di.DatabaseModule_ProvideJourneyProgressDaoFactory;
import com.within.app.di.DatabaseModule_ProvideWithinDatabaseFactory;
import com.within.app.notification.BootReceiver;
import com.within.app.notification.BootReceiver_MembersInjector;
import com.within.app.notification.DailyContentResolver;
import com.within.app.notification.NotificationReceiver;
import com.within.app.notification.NotificationReceiver_MembersInjector;
import com.within.app.notification.NotificationScheduler;
import com.within.app.ui.home.HomeViewModel;
import com.within.app.ui.home.HomeViewModel_HiltModules;
import com.within.app.ui.journey.JourneyDayViewModel;
import com.within.app.ui.journey.JourneyDayViewModel_HiltModules;
import com.within.app.ui.journey.JourneyMapViewModel;
import com.within.app.ui.journey.JourneyMapViewModel_HiltModules;
import com.within.app.ui.journey.JourneyTodayViewModel;
import com.within.app.ui.journey.JourneyTodayViewModel_HiltModules;
import com.within.app.ui.journey.ReflectionsViewModel;
import com.within.app.ui.journey.ReflectionsViewModel_HiltModules;
import com.within.app.ui.navigation.NavigationViewModel;
import com.within.app.ui.navigation.NavigationViewModel_HiltModules;
import com.within.app.ui.onboarding.OnboardingViewModel;
import com.within.app.ui.onboarding.OnboardingViewModel_HiltModules;
import com.within.app.ui.settings.SettingsViewModel;
import com.within.app.ui.settings.SettingsViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DaggerWithinApplication_HiltComponents_SingletonC {
  private DaggerWithinApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public WithinApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements WithinApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public WithinApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements WithinApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public WithinApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements WithinApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public WithinApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements WithinApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public WithinApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements WithinApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public WithinApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements WithinApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public WithinApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements WithinApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public WithinApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends WithinApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends WithinApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends WithinApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends WithinApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(8).put(LazyClassKeyProvider.com_within_app_ui_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_within_app_ui_journey_JourneyDayViewModel, JourneyDayViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_within_app_ui_journey_JourneyMapViewModel, JourneyMapViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_within_app_ui_journey_JourneyTodayViewModel, JourneyTodayViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_within_app_ui_navigation_NavigationViewModel, NavigationViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_within_app_ui_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_within_app_ui_journey_ReflectionsViewModel, ReflectionsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_within_app_ui_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectBillingManager(instance, singletonCImpl.billingManagerProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_within_app_ui_onboarding_OnboardingViewModel = "com.within.app.ui.onboarding.OnboardingViewModel";

      static String com_within_app_ui_journey_JourneyDayViewModel = "com.within.app.ui.journey.JourneyDayViewModel";

      static String com_within_app_ui_journey_JourneyTodayViewModel = "com.within.app.ui.journey.JourneyTodayViewModel";

      static String com_within_app_ui_settings_SettingsViewModel = "com.within.app.ui.settings.SettingsViewModel";

      static String com_within_app_ui_journey_JourneyMapViewModel = "com.within.app.ui.journey.JourneyMapViewModel";

      static String com_within_app_ui_navigation_NavigationViewModel = "com.within.app.ui.navigation.NavigationViewModel";

      static String com_within_app_ui_home_HomeViewModel = "com.within.app.ui.home.HomeViewModel";

      static String com_within_app_ui_journey_ReflectionsViewModel = "com.within.app.ui.journey.ReflectionsViewModel";

      @KeepFieldType
      OnboardingViewModel com_within_app_ui_onboarding_OnboardingViewModel2;

      @KeepFieldType
      JourneyDayViewModel com_within_app_ui_journey_JourneyDayViewModel2;

      @KeepFieldType
      JourneyTodayViewModel com_within_app_ui_journey_JourneyTodayViewModel2;

      @KeepFieldType
      SettingsViewModel com_within_app_ui_settings_SettingsViewModel2;

      @KeepFieldType
      JourneyMapViewModel com_within_app_ui_journey_JourneyMapViewModel2;

      @KeepFieldType
      NavigationViewModel com_within_app_ui_navigation_NavigationViewModel2;

      @KeepFieldType
      HomeViewModel com_within_app_ui_home_HomeViewModel2;

      @KeepFieldType
      ReflectionsViewModel com_within_app_ui_journey_ReflectionsViewModel2;
    }
  }

  private static final class ViewModelCImpl extends WithinApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<JourneyDayViewModel> journeyDayViewModelProvider;

    private Provider<JourneyMapViewModel> journeyMapViewModelProvider;

    private Provider<JourneyTodayViewModel> journeyTodayViewModelProvider;

    private Provider<NavigationViewModel> navigationViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<ReflectionsViewModel> reflectionsViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.journeyDayViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.journeyMapViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.journeyTodayViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.navigationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.reflectionsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(8).put(LazyClassKeyProvider.com_within_app_ui_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_within_app_ui_journey_JourneyDayViewModel, ((Provider) journeyDayViewModelProvider)).put(LazyClassKeyProvider.com_within_app_ui_journey_JourneyMapViewModel, ((Provider) journeyMapViewModelProvider)).put(LazyClassKeyProvider.com_within_app_ui_journey_JourneyTodayViewModel, ((Provider) journeyTodayViewModelProvider)).put(LazyClassKeyProvider.com_within_app_ui_navigation_NavigationViewModel, ((Provider) navigationViewModelProvider)).put(LazyClassKeyProvider.com_within_app_ui_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_within_app_ui_journey_ReflectionsViewModel, ((Provider) reflectionsViewModelProvider)).put(LazyClassKeyProvider.com_within_app_ui_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_within_app_ui_onboarding_OnboardingViewModel = "com.within.app.ui.onboarding.OnboardingViewModel";

      static String com_within_app_ui_journey_JourneyDayViewModel = "com.within.app.ui.journey.JourneyDayViewModel";

      static String com_within_app_ui_journey_JourneyTodayViewModel = "com.within.app.ui.journey.JourneyTodayViewModel";

      static String com_within_app_ui_journey_ReflectionsViewModel = "com.within.app.ui.journey.ReflectionsViewModel";

      static String com_within_app_ui_navigation_NavigationViewModel = "com.within.app.ui.navigation.NavigationViewModel";

      static String com_within_app_ui_home_HomeViewModel = "com.within.app.ui.home.HomeViewModel";

      static String com_within_app_ui_journey_JourneyMapViewModel = "com.within.app.ui.journey.JourneyMapViewModel";

      static String com_within_app_ui_settings_SettingsViewModel = "com.within.app.ui.settings.SettingsViewModel";

      @KeepFieldType
      OnboardingViewModel com_within_app_ui_onboarding_OnboardingViewModel2;

      @KeepFieldType
      JourneyDayViewModel com_within_app_ui_journey_JourneyDayViewModel2;

      @KeepFieldType
      JourneyTodayViewModel com_within_app_ui_journey_JourneyTodayViewModel2;

      @KeepFieldType
      ReflectionsViewModel com_within_app_ui_journey_ReflectionsViewModel2;

      @KeepFieldType
      NavigationViewModel com_within_app_ui_navigation_NavigationViewModel2;

      @KeepFieldType
      HomeViewModel com_within_app_ui_home_HomeViewModel2;

      @KeepFieldType
      JourneyMapViewModel com_within_app_ui_journey_JourneyMapViewModel2;

      @KeepFieldType
      SettingsViewModel com_within_app_ui_settings_SettingsViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.within.app.ui.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.messageRepositoryProvider.get(), singletonCImpl.userPreferencesProvider.get());

          case 1: // com.within.app.ui.journey.JourneyDayViewModel 
          return (T) new JourneyDayViewModel(singletonCImpl.journeyRepositoryProvider.get(), singletonCImpl.journeyProgressRepositoryProvider.get(), singletonCImpl.userPreferencesProvider.get(), singletonCImpl.billingManagerProvider.get(), viewModelCImpl.savedStateHandle);

          case 2: // com.within.app.ui.journey.JourneyMapViewModel 
          return (T) new JourneyMapViewModel(singletonCImpl.journeyRepositoryProvider.get(), singletonCImpl.journeyProgressRepositoryProvider.get());

          case 3: // com.within.app.ui.journey.JourneyTodayViewModel 
          return (T) new JourneyTodayViewModel(singletonCImpl.journeyRepositoryProvider.get(), singletonCImpl.journeyProgressRepositoryProvider.get(), singletonCImpl.userPreferencesProvider.get());

          case 4: // com.within.app.ui.navigation.NavigationViewModel 
          return (T) new NavigationViewModel(singletonCImpl.userPreferencesProvider.get());

          case 5: // com.within.app.ui.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.userPreferencesProvider.get(), singletonCImpl.notificationSchedulerProvider.get(), singletonCImpl.journeyRepositoryProvider.get(), singletonCImpl.journeyProgressRepositoryProvider.get());

          case 6: // com.within.app.ui.journey.ReflectionsViewModel 
          return (T) new ReflectionsViewModel(singletonCImpl.journeyRepositoryProvider.get(), singletonCImpl.journeyProgressRepositoryProvider.get());

          case 7: // com.within.app.ui.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.userPreferencesProvider.get(), singletonCImpl.notificationSchedulerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends WithinApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends WithinApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends WithinApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<UserPreferences> userPreferencesProvider;

    private Provider<NotificationScheduler> notificationSchedulerProvider;

    private Provider<Gson> provideGsonProvider;

    private Provider<JourneyRepository> journeyRepositoryProvider;

    private Provider<WithinDatabase> provideWithinDatabaseProvider;

    private Provider<JourneyProgressRepository> journeyProgressRepositoryProvider;

    private Provider<MessageRepository> messageRepositoryProvider;

    private Provider<DailyContentResolver> dailyContentResolverProvider;

    private Provider<BillingManager> billingManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private JourneyProgressDao journeyProgressDao() {
      return DatabaseModule_ProvideJourneyProgressDaoFactory.provideJourneyProgressDao(provideWithinDatabaseProvider.get());
    }

    private DayProgressDao dayProgressDao() {
      return DatabaseModule_ProvideDayProgressDaoFactory.provideDayProgressDao(provideWithinDatabaseProvider.get());
    }

    private JournalEntryDao journalEntryDao() {
      return DatabaseModule_ProvideJournalEntryDaoFactory.provideJournalEntryDao(provideWithinDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.userPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<UserPreferences>(singletonCImpl, 0));
      this.notificationSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<NotificationScheduler>(singletonCImpl, 1));
      this.provideGsonProvider = DoubleCheck.provider(new SwitchingProvider<Gson>(singletonCImpl, 4));
      this.journeyRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<JourneyRepository>(singletonCImpl, 3));
      this.provideWithinDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<WithinDatabase>(singletonCImpl, 6));
      this.journeyProgressRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<JourneyProgressRepository>(singletonCImpl, 5));
      this.messageRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<MessageRepository>(singletonCImpl, 7));
      this.dailyContentResolverProvider = DoubleCheck.provider(new SwitchingProvider<DailyContentResolver>(singletonCImpl, 2));
      this.billingManagerProvider = DoubleCheck.provider(new SwitchingProvider<BillingManager>(singletonCImpl, 8));
    }

    @Override
    public void injectWithinApplication(WithinApplication withinApplication) {
    }

    @Override
    public void injectBootReceiver(BootReceiver bootReceiver) {
      injectBootReceiver2(bootReceiver);
    }

    @Override
    public void injectNotificationReceiver(NotificationReceiver notificationReceiver) {
      injectNotificationReceiver2(notificationReceiver);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private BootReceiver injectBootReceiver2(BootReceiver instance) {
      BootReceiver_MembersInjector.injectUserPreferences(instance, userPreferencesProvider.get());
      BootReceiver_MembersInjector.injectNotificationScheduler(instance, notificationSchedulerProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private NotificationReceiver injectNotificationReceiver2(NotificationReceiver instance) {
      NotificationReceiver_MembersInjector.injectDailyContentResolver(instance, dailyContentResolverProvider.get());
      NotificationReceiver_MembersInjector.injectUserPreferences(instance, userPreferencesProvider.get());
      NotificationReceiver_MembersInjector.injectNotificationScheduler(instance, notificationSchedulerProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.within.app.data.preferences.UserPreferences 
          return (T) new UserPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.within.app.notification.NotificationScheduler 
          return (T) new NotificationScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.within.app.notification.DailyContentResolver 
          return (T) new DailyContentResolver(singletonCImpl.journeyRepositoryProvider.get(), singletonCImpl.journeyProgressRepositoryProvider.get(), singletonCImpl.messageRepositoryProvider.get(), singletonCImpl.userPreferencesProvider.get());

          case 3: // com.within.app.data.repository.JourneyRepository 
          return (T) new JourneyRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideGsonProvider.get());

          case 4: // com.google.gson.Gson 
          return (T) AppModule_ProvideGsonFactory.provideGson();

          case 5: // com.within.app.data.repository.JourneyProgressRepository 
          return (T) new JourneyProgressRepository(singletonCImpl.journeyProgressDao(), singletonCImpl.dayProgressDao(), singletonCImpl.journalEntryDao());

          case 6: // com.within.app.data.local.WithinDatabase 
          return (T) DatabaseModule_ProvideWithinDatabaseFactory.provideWithinDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.within.app.data.repository.MessageRepository 
          return (T) new MessageRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideGsonProvider.get());

          case 8: // com.within.app.billing.BillingManager 
          return (T) new BillingManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.userPreferencesProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
