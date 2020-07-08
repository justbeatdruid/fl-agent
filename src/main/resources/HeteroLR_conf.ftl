<#setting number_format="#">
{
  "initiator": {
    "role": "guest",
    "party_id": ${guest_party_id}
  },
  "job_parameters": {
    "work_mode": 1
  },
  "role": {
    "guest": [
      ${guest_party_id}
    ],
    "host": [
      <#list host_party_id_list as host>${host}<#if host_has_next>, </#if></#list>
    ],
    "arbiter": [
      ${guest_party_id}
    ]
  },
  "role_parameters": {
    "guest": {
      "args": {
        "data": {
          "train_data": [
            {
              "name": "table_${guest_party_id}_${data_type}",
              "namespace": "namespace_${federation_id}"
            }
          ],
          "eval_data": [
            {
              "name": "table_${guest_party_id}_${data_type}",
              "namespace": "namespace_${federation_id}"
            }
          ]
        }
      },
      "dataio_0": {
        "with_label": [true],
        "label_name": ["${label_name}"],
        "label_type": ["int"],
        "output_format": ["dense"],
        "missing_fill": [true],
        "outlier_replace": [true]
      },
      "evaluation_0": {
          "eval_type": ["binary"],
          "pos_label": [1]
      }
    },
    "host": {
      "args": {
        "data": {
          "train_data": [
			<#list host_party_id_list as host>
			{
              "name": "table_${host}_${data_type}",
              "namespace": "namespace_${federation_id}"
            }<#if host_has_next>,</#if>
			</#list>
          ],
          "eval_data": [
			<#list host_party_id_list as host>
			{
              "name": "table_${host}_${data_type}",
              "namespace": "namespace_${federation_id}"
            }<#if host_has_next>,</#if>
			</#list>
          ]
        }
      },
      "dataio_0": {
        "with_label": [<#list host_party_id_list as host>false<#if host_has_next>, </#if></#list>],
        "output_format": [<#list host_party_id_list as host>"dense"<#if host_has_next>, </#if></#list>],
        "outlier_replace": [<#list host_party_id_list as host>true<#if host_has_next>, </#if></#list>]
      },
      "evaluation_0": {
        "need_run": [<#list host_party_id_list as host>false<#if host_has_next>, </#if></#list>]
      }
    }
  },
  "algorithm_parameters": {
    "algorithm_0": {
      "penalty": "L2",
      "optimizer": "nesterov_momentum_sgd",
      "tol": #{loss ;m0M6},
      "alpha": 0.01,
      "max_iter": ${iteration?if_exists?string.number},
      "early_stop": "weight_diff",
      "batch_size": -1,
      "learning_rate": #{learning_rate ;m0M6},
      "init_param": {
        "init_method": "random_uniform"
      },
      "sqn_param": {
        "update_interval_L": 3,
        "memory_M": 5,
        "sample_size": 5000,
        "random_seed": null
      },
      "cv_param": {
        "n_splits": 5,
        "shuffle": false,
        "random_seed": 103,
        "need_cv": false,
        "evaluate_param": {
          "eval_type": "binary"
        }
      }
    },
    "intersect_0": {
      "intersect_method": "rsa",
      "sync_intersect_ids": true,
      "only_output_key": false
    }
  }
}
