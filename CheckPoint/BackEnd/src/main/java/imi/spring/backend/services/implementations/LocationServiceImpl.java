package imi.spring.backend.services.implementations;

import imi.spring.backend.models.Location;
import imi.spring.backend.repositories.LocationRepository;
import imi.spring.backend.services.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public List<Location> getLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public String deleteLocation(Long id) {
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
            return "Location has been successfully deleted.";
        }
        return "Location with that id does not exist!";
    }

    @Override
    public Location getLocationById(Long id) {
        return locationRepository.findById(id).orElse(null);
    }

    @Override
    public List<Location> getLocationsByNameKeyword(String name) {
        return locationRepository.findByNameLike("%" + name + "%");
    }
}
