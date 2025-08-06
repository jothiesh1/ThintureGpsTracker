package com.GpsTracker.Thinture.repository;

import com.GpsTracker.Thinture.model.CoordinatesCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoordinatesCacheRepository extends JpaRepository<CoordinatesCache, Long> {
    
    /**
     * 🎯 Main method: Find cached address by exact latitude and longitude
     * Used by frontend "Get Address" button
     */
    Optional<CoordinatesCache> findByLatitudeAndLongitude(Double latitude, Double longitude);
    
    /**
     * 🔍 Find cached address by rounded coordinates (for nearby locations)
     * This helps reduce cache misses for very close GPS points
     * Rounds to 4 decimal places (~10 meter accuracy)
     */
    @Query("SELECT cc FROM CoordinatesCache cc WHERE " +
           "ROUND(cc.latitude, 4) = ROUND(:latitude, 4) AND " +
           "ROUND(cc.longitude, 4) = ROUND(:longitude, 4) AND " +
           "cc.address IS NOT NULL AND cc.address != ''")
    Optional<CoordinatesCache> findByRoundedCoordinates(@Param("latitude") Double latitude, 
                                                       @Param("longitude") Double longitude);
    
    /**
     * ✅ Check if coordinates exist in cache (for duplicate prevention)
     * Used when GPS data arrives to avoid saving duplicate coordinates
     */
    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);
    
    /**
     * 📊 Count total cached coordinates
     * Used for cache statistics
     */
    @Query("SELECT COUNT(cc) FROM CoordinatesCache cc")
    long countCachedCoordinates();
    
    /**
     * 📊 Count coordinates that have addresses vs those that don't
     */
    @Query("SELECT COUNT(cc) FROM CoordinatesCache cc WHERE cc.address IS NOT NULL AND cc.address != ''")
    long countCoordinatesWithAddresses();
    
    @Query("SELECT COUNT(cc) FROM CoordinatesCache cc WHERE cc.address IS NULL OR cc.address = ''")
    long countCoordinatesWithoutAddresses();
    
    /**
     * 📋 Find coordinates that don't have addresses yet
     * Used for bulk address population
     */
    @Query("SELECT cc FROM CoordinatesCache cc WHERE cc.address IS NULL OR cc.address = '' ORDER BY cc.createdAt DESC")
    List<CoordinatesCache> findCoordinatesWithoutAddresses();
    
    /**
     * 📋 Find coordinates that don't have addresses (with limit)
     * Used for batch processing to avoid memory issues
     */
    @Query("SELECT cc FROM CoordinatesCache cc WHERE cc.address IS NULL OR cc.address = '' ORDER BY cc.createdAt DESC")
    List<CoordinatesCache> findCoordinatesWithoutAddresses(org.springframework.data.domain.Pageable pageable);
    
    /**
     * 🔍 Find coordinates within a specific area (for geographic queries)
     * Useful for finding nearby cached locations
     */
    @Query("SELECT cc FROM CoordinatesCache cc WHERE " +
           "cc.latitude BETWEEN :minLat AND :maxLat AND " +
           "cc.longitude BETWEEN :minLon AND :maxLon AND " +
           "cc.address IS NOT NULL AND cc.address != ''")
    List<CoordinatesCache> findCoordinatesInArea(@Param("minLat") Double minLatitude,
                                                @Param("maxLat") Double maxLatitude,
                                                @Param("minLon") Double minLongitude,
                                                @Param("maxLon") Double maxLongitude);
    
    /**
     * 🧹 Delete old cache entries (cleanup maintenance)
     * Remove entries older than specified date
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CoordinatesCache cc WHERE cc.createdAt < :cutoffDate")
    int deleteOldEntries(@Param("cutoffDate") Timestamp cutoffDate);
    
    /**
     * 🧹 Delete coordinates without addresses older than specified date
     * Cleanup coordinates that were saved but never had addresses fetched
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CoordinatesCache cc WHERE " +
           "(cc.address IS NULL OR cc.address = '') AND cc.createdAt < :cutoffDate")
    int deleteOldCoordinatesWithoutAddresses(@Param("cutoffDate") Timestamp cutoffDate);
    
    /**
     * 📈 Get cache hit rate statistics
     * Find coordinates that have been accessed multiple times (popular locations)
     */
    @Query("SELECT cc FROM CoordinatesCache cc WHERE cc.address IS NOT NULL AND cc.address != '' " +
           "ORDER BY cc.updatedAt DESC")
    List<CoordinatesCache> findRecentlyUsedCoordinates(org.springframework.data.domain.Pageable pageable);
    
    /**
     * 🔍 Search coordinates by address (reverse lookup)
     * Find coordinates for a specific address pattern
     */
    @Query("SELECT cc FROM CoordinatesCache cc WHERE " +
           "cc.address IS NOT NULL AND LOWER(cc.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<CoordinatesCache> findByAddressContaining(@Param("searchTerm") String searchTerm);
    
    /**
     * 📊 Get coordinates grouped by area (for analytics)
     * Count coordinates in different geographic regions
     */
    @Query("SELECT " +
           "FLOOR(cc.latitude) as latRegion, " +
           "FLOOR(cc.longitude) as lonRegion, " +
           "COUNT(cc) as coordinateCount " +
           "FROM CoordinatesCache cc " +
           "WHERE cc.address IS NOT NULL AND cc.address != '' " +
           "GROUP BY FLOOR(cc.latitude), FLOOR(cc.longitude) " +
           "ORDER BY coordinateCount DESC")
    List<Object[]> getCoordinatesByRegion();
    
    /**
     * 🎯 Find duplicate coordinates (should be empty due to unique constraint)
     * Useful for debugging and verification
     */
    @Query("SELECT cc.latitude, cc.longitude, COUNT(cc) as duplicateCount " +
           "FROM CoordinatesCache cc " +
           "GROUP BY cc.latitude, cc.longitude " +
           "HAVING COUNT(cc) > 1")
    List<Object[]> findDuplicateCoordinates();
    
    /**
     * 📊 Get cache efficiency metrics
     * Find coordinates with addresses vs total coordinates
     */
    @Query("SELECT " +
           "COUNT(cc) as totalCoordinates, " +
           "SUM(CASE WHEN cc.address IS NOT NULL AND cc.address != '' THEN 1 ELSE 0 END) as withAddresses, " +
           "SUM(CASE WHEN cc.address IS NULL OR cc.address = '' THEN 1 ELSE 0 END) as withoutAddresses " +
           "FROM CoordinatesCache cc")
    Object[] getCacheEfficiencyStats();
    
    /**
     * 🔍 Find coordinates created within date range
     * Useful for monitoring GPS data collection over time
     */
    @Query("SELECT cc FROM CoordinatesCache cc WHERE " +
           "cc.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY cc.createdAt DESC")
    List<CoordinatesCache> findCoordinatesInDateRange(@Param("startDate") Timestamp startDate,
                                                     @Param("endDate") Timestamp endDate);
    
    /**
     * 🎯 Update address for existing coordinates
     * Used when filling in addresses for coordinates saved from GPS data
     */
    @Modifying
    @Transactional
    @Query("UPDATE CoordinatesCache cc SET cc.address = :address, cc.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE cc.latitude = :latitude AND cc.longitude = :longitude")
    int updateAddressForCoordinates(@Param("latitude") Double latitude, 
                                   @Param("longitude") Double longitude, 
                                   @Param("address") String address);
}