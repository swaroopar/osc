package org.eclipse.xpanse.modules.models.billing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

@ExtendWith(MockitoExtension.class)
class ResourceUsageTest {

    @Mock
    private Price mockLicensePrice;
    @Mock
    private Price mockMarkUpPrice;
    @Mock
    private List<Resource> mockResources;

    private ResourceUsage resourceUsageUnderTest;

    @BeforeEach
    void setUp() {
        resourceUsageUnderTest = new ResourceUsage();
        resourceUsageUnderTest.setLicensePrice(mockLicensePrice);
        resourceUsageUnderTest.setMarkUpPrice(mockMarkUpPrice);
        resourceUsageUnderTest.setResources(mockResources);
    }

    @Test
    void testGetLicensePrice() {
        assertThat(resourceUsageUnderTest.getLicensePrice()).isEqualTo(mockLicensePrice);
        assertThat(resourceUsageUnderTest.getMarkUpPrice()).isEqualTo(mockMarkUpPrice);
        assertThat(resourceUsageUnderTest.getResources()).isEqualTo(mockResources);
    }

    @Test
    void testEqualsAndHashCode() {
        Object o = new Object();
        assertThat(resourceUsageUnderTest.equals(o)).isFalse();
        assertThat(resourceUsageUnderTest.canEqual(o)).isFalse();
        assertThat(resourceUsageUnderTest.hashCode()).isNotEqualTo(o.hashCode());

        ResourceUsage resourceUsage = new ResourceUsage();
        assertThat(resourceUsageUnderTest).isNotEqualTo(resourceUsage);
        assertThat(resourceUsageUnderTest.canEqual(resourceUsage)).isTrue();
        assertThat(resourceUsageUnderTest.hashCode()).isNotEqualTo(resourceUsage.hashCode());

        BeanUtils.copyProperties(resourceUsageUnderTest, resourceUsage);
        assertThat(resourceUsageUnderTest).isEqualTo(resourceUsage);
        assertThat(resourceUsageUnderTest.hashCode()).isEqualTo(resourceUsage.hashCode());
    }

    @Test
    void testToString() {
        String result = "ResourceUsage("
                + "resources=" + mockResources
                + ", licensePrice=" + mockLicensePrice
                + ", markUpPrice=" + mockMarkUpPrice
                + ")";
        assertThat(resourceUsageUnderTest.toString()).isEqualTo(result);
    }
}
