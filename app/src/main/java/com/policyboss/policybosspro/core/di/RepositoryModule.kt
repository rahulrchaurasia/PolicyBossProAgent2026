package com.policyboss.policybosspro.core.di

import com.policyboss.policybosspro.core.repository.contact.ContactRepository
import com.policyboss.policybosspro.core.repository.contact.ContactRepositoryImpl
import com.policyboss.policybosspro.core.repository.home.HomeRepository
import com.policyboss.policybosspro.core.repository.home.HomeRepositoryImpl
import com.policyboss.policybosspro.core.repository.lead.LeadRepository
import com.policyboss.policybosspro.core.repository.lead.LeadRepositoryImpl
import com.policyboss.policybosspro.core.repository.dashboard.DashboardRepository
import com.policyboss.policybosspro.core.repository.dashboard.DashboardRepositoryImpl
import com.policyboss.policybosspro.core.repository.sales.SalesRepository
import com.policyboss.policybosspro.core.repository.sales.SalesRepositoryImpl
import com.policyboss.policybosspro.core.repository.grow.GrowRepository
import com.policyboss.policybosspro.core.repository.grow.GrowRepositoryImpl
import com.policyboss.policybosspro.core.repository.login.LoginRepository
import com.policyboss.policybosspro.core.repository.login.LoginRepositoryImpl
import com.policyboss.policybosspro.core.repository.profile.ProfileRepository
import com.policyboss.policybosspro.core.repository.profile.ProfileRepositoryImpl
import com.policyboss.policybosspro.core.repository.register.RegisterRepository
import com.policyboss.policybosspro.core.repository.register.RegisterRepositoryImpl
import com.policyboss.policybosspro.core.repository.syncContact.SyncContactRepository
import com.policyboss.policybosspro.core.repository.syncContact.SyncContactRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindLeadRepository(
        leadRepositoryImpl: LeadRepositoryImpl
    ): LeadRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(
        dashboardRepositoryImpl: DashboardRepositoryImpl
    ): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindSalesRepository(
        salesRepositoryImpl: SalesRepositoryImpl
    ): SalesRepository

    @Binds
    @Singleton
    abstract fun bindGrowRepository(
        growRepositoryImpl: GrowRepositoryImpl
    ): GrowRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(
        registerRepositoryImpl: RegisterRepositoryImpl
    ): RegisterRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(
        contactRepositoryImpl: ContactRepositoryImpl
    ): ContactRepository

    @Binds
    @Singleton
    abstract fun bindSyncContactRepository(
        syncContactRepositoryImpl: SyncContactRepositoryImpl
    ): SyncContactRepository
}
