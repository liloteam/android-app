package org.mozilla.fenix.lilomodule.components.metrics

import mozilla.components.support.base.log.logger.Logger
import org.mozilla.fenix.components.metrics.Event
import org.mozilla.fenix.components.metrics.MetricController
import org.mozilla.fenix.components.metrics.MetricServiceType
import org.mozilla.fenix.components.metrics.MetricsService
import org.mozilla.fenix.utils.Settings

class LiloReleaseMetricController(
    private val services: List<MetricsService>,
    private val isDataTelemetryEnabled: () -> Boolean,
    private val isMarketingDataTelemetryEnabled: () -> Boolean,
    private val isUsageTelemetryEnabled: () -> Boolean,
    private val settings: Settings,
    private val logger: Logger = Logger(),
) : MetricController {

    override fun start(type: MetricServiceType) {
        logger.debug("DebugMetricController: start")
    }

    override fun stop(type: MetricServiceType) {
        logger.debug("DebugMetricController: stop")
    }

    override fun track(event: Event) {
        logger.debug("DebugMetricController: track event: $event")
    }
}
