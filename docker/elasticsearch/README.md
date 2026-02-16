# ELK Stack for Logistics Platform

This directory contains the Elasticsearch, Logstash, Kibana (ELK) stack configuration for log aggregation, search, and monitoring.

## Quick Start

1. **Start the ELK stack:**
   ```bash
   docker-compose -f docker-compose-elk.yml up -d
   ```

2. **Verify services are running:**
   ```bash
   docker-compose -f docker-compose-elk.yml ps
   ```

3. **Access Kibana:**
   - URL: http://localhost:5601
   - Wait 1-2 minutes for Kibana to start

4. **Check Elasticsearch health:**
   ```bash
   curl http://localhost:9200/_cluster/health
   ```

## Services

| Service | Port | Description |
|---------|------|-------------|
| Elasticsearch | 9200, 9300 | Search and analytics engine |
| Kibana | 5601 | Visualization and UI |
| Logstash | 5044, 9600 | Log processing pipeline |
| Filebeat | - | Log shipper |
| APM Server | 8200 | Application performance monitoring |

## Configuration Files

- `elasticsearch.yml` - Elasticsearch cluster configuration
- `logstash.conf` - Logstash pipeline configuration
- `logstash.yml` - Logstash service configuration
- `filebeat.yml` - Filebeat log shipping configuration
- `apm-server.yml` - APM Server configuration

## Kibana Dashboards

After starting the stack, import pre-configured dashboards:

1. Open Kibana (http://localhost:5601)
2. Go to **Management > Stack Management > Kibana > Saved Objects**
3. Import dashboards from `dashboards/` directory

## Index Patterns

The following index patterns are created:

- `logistics-logs-*` - Application logs
- `apm-*` - APM metrics and traces

## Maintenance

### Stop services:
```bash
docker-compose -f docker-compose-elk.yml down
```

### Stop and remove data:
```bash
docker-compose -f docker-compose-elk.yml down -v
```

### View logs:
```bash
docker-compose -f docker-compose-elk.yml logs -f elasticsearch
docker-compose -f docker-compose-elk.yml logs -f kibana
```

### Restart a service:
```bash
docker-compose -f docker-compose-elk.yml restart elasticsearch
```

## Troubleshooting

### Elasticsearch won't start
- Check available memory: Elasticsearch requires at least 1GB RAM
- Increase vm.max_map_count: `sudo sysctl -w vm.max_map_count=262144`

### Kibana connection refused
- Wait for Elasticsearch to be healthy (check health endpoint)
- Verify Elasticsearch is accessible from Kibana container

### No logs appearing
- Check Filebeat is running: `docker-compose -f docker-compose-elk.yml logs filebeat`
- Verify log paths are correct in `filebeat.yml`
- Check Logstash pipeline: `docker-compose -f docker-compose-elk.yml logs logstash`

## Production Considerations

For production deployment:

1. **Enable Security:**
   - Set `xpack.security.enabled: true` in elasticsearch.yml
   - Configure SSL/TLS certificates
   - Set up user authentication

2. **Scale Elasticsearch:**
   - Use multi-node cluster
   - Configure proper shard and replica settings
   - Set up dedicated master nodes

3. **Resource Allocation:**
   - Increase heap size based on available memory
   - Configure proper ulimits
   - Use SSD storage for data volumes

4. **Monitoring:**
   - Enable X-Pack monitoring
   - Set up alerting for cluster health
   - Configure log retention policies

5. **Backups:**
   - Set up snapshot repository
   - Configure automated backup schedules
   - Test disaster recovery procedures
