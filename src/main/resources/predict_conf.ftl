<#setting number_format="#">
{
  "initiator": {
    "role": "guest",
    "party_id": ${guest_party_id}
  },
  "job_parameters": {
    "work_mode": 1,
    "job_type": "predict",
    "model_id": ${model_id},
    "model_version": ${model_version}
  },
  "role": {
    "guest": [
      ${guest_party_id}
    ],
    "host": [
      <#list host_party_id_list as host>host<#if host_has_next>, </#if></#list>
    ],
    "arbiter": [
      ${guest_party_id}
    ]
  },
  "role_parameters": {
    "guest": {
      "args": {
        "data": {
          "eval_data": [
            {
              "name": "table_${guest_party_id}_${data_type}",
              "namespace": "namespace_${federation_id}"
            }
          ]
        }
      }
    },
    "host": {
      "args": {
        "data": {
          "eval_data": [
			<#list host_party_id_list as host>
			{
              "name": "table_${host}_${data_type}",
              "namespace": "namespace_${federation_id}"
            }<#if host_has_next>,</#if>
			</#list>
          ]
        }
      }
    }
  }
}
