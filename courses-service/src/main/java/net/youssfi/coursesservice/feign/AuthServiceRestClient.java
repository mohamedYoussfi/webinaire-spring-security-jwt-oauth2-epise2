package net.youssfi.coursesservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthServiceRestClient {
     @PostMapping("/admin/requestForRoleAttribution")
     AppRole addRoleToUser(@RequestBody AddRoleToUserDTO request);
}
