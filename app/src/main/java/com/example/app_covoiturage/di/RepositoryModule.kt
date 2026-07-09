package com.example.app_covoiturage.di

import com.example.app_covoiturage.data.repository.AuthRepositoryImpl
import com.example.app_covoiturage.data.repository.TripRepositoryImpl
import com.example.app_covoiturage.data.repository.VehicleRepositoryImpl
import com.example.app_covoiturage.domain.repository.AuthRepository
import com.example.app_covoiturage.domain.repository.TripRepository
import com.example.app_covoiturage.domain.repository.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.app_covoiturage.data.repository.ReservationRepositoryImpl
import com.example.app_covoiturage.domain.repository.ReservationRepository
import com.example.app_covoiturage.data.repository.*
import com.example.app_covoiturage.domain.repository.GeocodingRepository
import com.example.app_covoiturage.domain.repository.RouteRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindGeocodingRepository(impl: GeocodingRepositoryImpl): GeocodingRepository

    @Binds
    @Singleton
    abstract fun bindReservationRepository(impl: ReservationRepositoryImpl): ReservationRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(impl: RouteRepositoryImpl): RouteRepository

}